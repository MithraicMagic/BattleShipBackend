package com.bs.epic.battleships;

import java.util.concurrent.atomic.AtomicInteger;

import com.bs.epic.battleships.events.AutoPlaceShips;
import com.bs.epic.battleships.events.Code;
import com.bs.epic.battleships.events.DonePlacing;
import com.bs.epic.battleships.events.ErrorEvent;
import com.bs.epic.battleships.events.GameData;
import com.bs.epic.battleships.events.HitMissData;
import com.bs.epic.battleships.events.LeaveLobby;
import com.bs.epic.battleships.events.LobbyId;
import com.bs.epic.battleships.events.LoggedInUserWon;
import com.bs.epic.battleships.events.Message;
import com.bs.epic.battleships.events.Messages;
import com.bs.epic.battleships.events.Name;
import com.bs.epic.battleships.events.NameAccepted;
import com.bs.epic.battleships.events.NameData;
import com.bs.epic.battleships.events.PlaceShip;
import com.bs.epic.battleships.events.Reconnect;
import com.bs.epic.battleships.events.Rematch;
import com.bs.epic.battleships.events.RemoveShip;
import com.bs.epic.battleships.events.SetupData;
import com.bs.epic.battleships.events.Ships;
import com.bs.epic.battleships.events.Shoot;
import com.bs.epic.battleships.events.StartSinglePlayerLobby;
import com.bs.epic.battleships.events.Uid;
import com.bs.epic.battleships.game.grid.GridPos;
import com.bs.epic.battleships.lobby.Lobby;
import com.bs.epic.battleships.lobby.LobbyManager;
import com.bs.epic.battleships.rest.service.AuthService;
import com.bs.epic.battleships.rest.service.MessageService;
import com.bs.epic.battleships.user.User;
import com.bs.epic.battleships.user.UserManager;
import com.bs.epic.battleships.user.UserState;
import com.bs.epic.battleships.user.UserType;
import com.bs.epic.battleships.user.ai.AIPlayer;
import com.bs.epic.battleships.user.player.Player;
import com.bs.epic.battleships.util.Util;
import com.bs.epic.battleships.util.result.ShootSuccess;
import com.bs.epic.battleships.verification.AuthValidator;
import com.bs.epic.battleships.verification.JwtUtil;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;

public class SocketEvents {
    private final UserManager userManager;
    private final LobbyManager lobbyManager;

    private final JwtUtil jwtUtil;
    private final AuthValidator authValidator;
    private final AuthService authService;
    private final MessageService messageService;

    private AtomicInteger ids;

    public SocketEvents(JwtUtil jwtUtil, UserManager userManager, LobbyManager lobbyManager,
            AuthValidator authValidator, AuthService authService, MessageService messageService)
    {
        this.jwtUtil = jwtUtil;
        this.userManager = userManager;
        this.lobbyManager = lobbyManager;
        this.authValidator = authValidator;
        this.authService = authService;
        this.messageService = messageService;

        this.ids = new AtomicInteger();
    }

    public void onLastUid(SocketIOClient socket, Uid data, AckRequest ackRequest) {
        var user = userManager.getUser(data.uid);
        if (user != null && user.type == UserType.Player) {
            var player = (Player) user;
            var lobby = lobbyManager.getLobbyByUid(data.uid);

            player.onReconnect(socket);
            player.setThread(getDisconnectThread(player));

            if (lobby != null) {
                lobby.onPlayerReconnect(player);
            }
            else {
                socket.sendEvent("reconnect", new Reconnect(player.name, player.code));
            }
        }
        else {
            userManager.add(new User(socket));
        }
    }

    public void onInputUsername(SocketIOClient socket, Name data, AckRequest ackRequest) {
        var result = authValidator.verifyUsername(data.name);
        if (result.success) {
            if (userManager.nameExists(data.name)) {
                socket.sendEvent("errorEvent", new ErrorEvent("inputUsername", "This username is already in use"));
                return;
            }

            var player = new Player(data.name, socket, Util.generateNewCode(5));
            player.setThread(getDisconnectThread(player));
            userManager.replaceUserByPlayer(player);

            socket.sendEvent("nameAccepted", new NameAccepted(player.code, player.uid, player.name));
        }
        else {
            socket.sendEvent("errorEvent", result.getError());
        }
    }

    public void onGetLobbyInfo(SocketIOClient socket, Code data, AckRequest ackRequest) {
        var user = userManager.getByCode(data.code);

        if (user == null) {
            socket.sendEvent("errorEvent", new ErrorEvent("getLobbyInfo", "User does not exist"));
            return;
        }

        socket.sendEvent("lobbyInfo", new Name(user.name));
    }

    public void onTryCode(SocketIOClient socket, Code data, AckRequest ackRequest) {
        var cur = userManager.getBySocket(socket);
        var other = userManager.getByCode(data.code);

        if (cur == null) {
            socket.sendEvent("errorEvent", new ErrorEvent("tryCode", "Something went horribly wrong. Try refreshing the page."));
            return;
        }

        if (cur == other) {
            socket.sendEvent("errorEvent", new ErrorEvent("tryCode", "You can't enter your own lobby."));
            return;
        }

        if (cur.type == UserType.Player && cur.state == UserState.Available && other != null) {
            var current = (Player) cur;

            var lobby = new Lobby(ids.incrementAndGet(), other, current);
            lobbyManager.add(lobby);
            lobby.sendLobbyJoinedEvent();
        } else {
            socket.sendEvent("errorEvent", new ErrorEvent("tryCode", "You did not enter a valid code!"));
        }
    }

    public void onSinglePlayerSettings(SocketIOClient socket, Uid data, AckRequest ackRequest) {
        var player = userManager.getPlayer(data.uid);
        if (player == null) {
            socket.sendEvent("errorEvent", new ErrorEvent("singlePlayerSettings", "Invalid player."));
            return;
        }

        player.setState(UserState.Settings);
    }

    public void onStartSinglePlayerLobby(SocketIOClient socket, StartSinglePlayerLobby data, AckRequest ackRequest) {
        var player = userManager.getPlayer(data.uid);
        if (player == null) {
            socket.sendEvent("errorEvent", new ErrorEvent("startSinglePlayerLobby", "Invalid player."));
            return;
        }

        if (data.time < 100 || data.time > 10000) {
            socket.sendEvent("errorEvent", new ErrorEvent("startSinglePlayerLobby", "Delay should be between 100ms and 10000ms"));
            return;
        }

        if (data.difficulty < 1 || data.difficulty > 3) {
            socket.sendEvent("errorEvent", new ErrorEvent("startSinglePlayerLobby", "Invalid difficulty"));
            return;
        }

        var opponent = new AIPlayer(data.time, data.difficulty, messageService.getAiMessages());
        var lobby = new Lobby(ids.incrementAndGet(), player, opponent);
        opponent.lobby = lobby;
        lobbyManager.add(lobby);
        lobby.sendLobbyJoinedEvent();
    }

    public void onLeaveLobby(SocketIOClient socket, LeaveLobby data, AckRequest ackRequest) {
        var lobby = lobbyManager.getLobby(data.lobbyId);
        var user = userManager.getUser(data.uid);

        if (user == null) {
            socket.sendEvent("errorEvent", new ErrorEvent("leaveLobby", "Something went horribly wrong. Try refreshing the page."));
            return;
        }

        if (lobby == null) {
            socket.sendEvent("errorEvent", new ErrorEvent("leaveLobby", "You tried to leave a lobby that doesn't exist."));
            return;
        }

        if (!lobby.hasPlayer(data.uid)) {
            socket.sendEvent("errorEvent", new ErrorEvent("leaveLobby", "You tried leaving a lobby that you're not a part of."));
            return;
        }

        var player = (Player) user;
        player.setState(UserState.Available);

        lobby.onPlayerLeave(player);
        lobbyManager.remove(lobby);
        socket.sendEvent("lobbyLeft");
    }

    public void onStartup(SocketIOClient socket, LobbyId data, AckRequest ackRequest) {
        var lobby = lobbyManager.getLobby(data.id);
        if (lobby == null) {
            socket.sendEvent("errorEvent", new ErrorEvent("startGame", "Invalid lobby."));
            return;
        }

        lobby.initGame(10);
        lobby.sendEventToLobby("setupStarted");
    }

    public void onPlaceShip(SocketIOClient socket, PlaceShip data, AckRequest ackRequest) {
        var lobby = lobbyManager.getLobby(data.lobbyId);
        if (lobby == null) {
            socket.sendEvent("errorEvent", new ErrorEvent("placeShip", "Invalid lobby."));
            return;
        }

        var result = lobby.game.placeShip(lobby.getPlayer(data.uid), data.ship, new GridPos(data.i, data.j), data.horizontal);
        if (result.success) {
            socket.sendEvent("placeShipAccepted");
        }
        else {
            socket.sendEvent("errorEvent", result.getError());
        }
    }

    public void onAutoPlaceShips(SocketIOClient socket, AutoPlaceShips data, AckRequest ackRequest) {
        var lobby = lobbyManager.getLobby(data.lobbyId);
        if (lobby == null) {
            socket.sendEvent("errorEvent", new ErrorEvent("autoPlaceShips", "Invalid lobby."));
            return;
        }

        var player = lobby.getPlayer(data.uid);
        if (player == null) {
            socket.sendEvent("errorEvent", new ErrorEvent("autoPlaceShips", "Invalid player."));
            return;
        }

        var result = lobby.game.autoPlaceShips(player);
        if (result.success) {
            socket.sendEvent("autoPlaceShipsAccepted", new Ships(player.ships.values()));
        }
    }

    public void onRemoveShip(SocketIOClient socket, RemoveShip data, AckRequest ackRequest) {
        var lobby = lobbyManager.getLobby(data.lobbyId);
        if (lobby == null) {
            socket.sendEvent("errorEvent", new ErrorEvent("removeShip", "Invalid lobby."));
            return;
        }

        lobby.game.removeShip(data.ship, userManager.getPlayer(data.uid));
        socket.sendEvent("removeShipAccepted");
    }

    public void onSubmitSetup(SocketIOClient socket, DonePlacing data, AckRequest ackRequest) {
        var lobby = lobbyManager.getLobby(data.lobbyId);
        if (lobby == null) {
            socket.sendEvent("errorEvent", new ErrorEvent("submitSetup", "Invalid lobby."));
            return;
        }

        var result = lobby.donePlacing(data.uid);
        if (result.success) {
            socket.sendEvent("setupAccepted");
        }
        else {
            socket.sendEvent("errorEvent", result.getError());
        }
    }

    public void onShoot(SocketIOClient socket, Shoot data, AckRequest ackRequest) {
        var lobby = lobbyManager.getLobby(data.lobbyId);
        if (lobby == null) {
            socket.sendEvent("errorEvent", new ErrorEvent("shoot", "Invalid lobby."));
            return;
        }

        var result = lobby.shoot(data.uid, new GridPos(data.i, data.j));
        if (result.success) {
            var suc = (ShootSuccess) result;
            socket.sendEvent("shotFired", suc.result);
        }
        else {
            socket.sendEvent("errorEvent", result.getError());
        }
    }

    public void onGetSetupData(SocketIOClient socket, Uid data, AckRequest ackRequest) {
        var u = userManager.getUser(data.uid);
        if (u == null || u.type == UserType.User) return;

        var p = (Player) u;
        var lobby = lobbyManager.getLobbyByUid(p.uid);
        if (lobby == null) return;

        socket.sendEvent("setupData", new SetupData(
                lobby.id, p.name, lobby.getOtherPlayer(p).name, p.leader, p.getShips()
        ));
    }

    public void onGetNameData(SocketIOClient socket, Uid data, AckRequest ackRequest) {
        var u = userManager.getUser(data.uid);
        if (u == null || u.type == UserType.User) return;

        var p = (Player) u;
        socket.sendEvent("nameData", new NameData(p.code, p.name));
    }

    public void onGetGameData(SocketIOClient socket, Uid data, AckRequest ackRequest) {
        var u = userManager.getUser(data.uid);
        if (u == null || u.type == UserType.User) return;

        var p = (Player) u;
        var l = lobbyManager.getLobbyByUid(data.uid);
        if (l == null) return;

        var other = l.getOtherPlayer(p);

        socket.sendEvent("gameData",
                new GameData(
                        l.id, p.name, l.getOtherPlayer(p).name, p.leader, p.getShips(),
                        new HitMissData(p.hits, other.hits), new HitMissData(p.misses, other.misses), other.boatsLeft
                )
        );
    }

    public void onSendMessage(SocketIOClient socket, Message data, AckRequest ackRequest) {
        var player = userManager.getPlayer(data.uid);
        if (player == null) {
            socket.sendEvent("errorEvent", new ErrorEvent("sendMessage", "Invalid player."));
            return;
        }

        var lobby = lobbyManager.getLobbyByUid(data.uid);
        if (lobby == null) {
            socket.sendEvent("errorEvent", new ErrorEvent("sendMessage", "Invalid lobby."));
            return;
        }

        var result = lobby.sendMessage(data.message, player);
        if (!result.success) socket.sendEvent("errorEvent", result.getError());
    }

    public void onGetMessages(SocketIOClient socket, Uid data, AckRequest ackRequest) {
        var player = userManager.getPlayer(data.uid);
        var lobby = lobbyManager.getLobbyByUid(data.uid);

        if (player == null) {
            socket.sendEvent("errorEvent", new ErrorEvent("getMessages", "Invalid player."));
            return;
        }

        if (lobby == null) {
            socket.sendEvent("errorEvent", new ErrorEvent("getMessages", "Invalid lobby."));
            return;
        }

        socket.sendEvent("messages", new Messages(lobby.getMessages()));
    }

    public void onRematch(SocketIOClient socket, Rematch data, AckRequest ackRequest) {
        var player = userManager.getPlayer(data.uid);
        if (player == null) {
            socket.sendEvent("errorEvent", new ErrorEvent("rematch", "Invalid player."));
            return;
        }

        var lobby = lobbyManager.getLobbyByUid(data.uid);
        if (lobby == null) {
            socket.sendEvent("errorEvent", new ErrorEvent("rematch", "Invalid lobby."));
            return;
        }

        lobby.onRematchRequest(player);
    }

    public void onLoggedInUserWon(SocketIOClient socket, LoggedInUserWon data, AckRequest ackRequest) {
        var username = jwtUtil.extractUsername(data.jwt);
        if (username == null) {
            socket.sendEvent("errorEvent", new ErrorEvent("loggedInUserWon", "Invalid token."));
            return;
        }

        var lobby = lobbyManager.getLobby(data.lobbyId);
        if (lobby == null) {
            socket.sendEvent("errorEvent", new ErrorEvent("loggedInUserWon", "Invalid lobby."));
            return;
        }

        var otherPlayer = lobby.getOtherPlayer(data.uid);
        var isSingleplayer = (otherPlayer instanceof AIPlayer);

        var oUser = authService.getByUsername(username);
        if (oUser.isPresent()) {
            var user = oUser.get();
            if (isSingleplayer) user.spWins++;
            else user.mpWins++;

            authService.saveUser(user);
            return;
        }

        socket.sendEvent("errorEvent", new ErrorEvent("loggedInUserWon", "Invalid player"));
    }

    private Thread getDisconnectThread(User u) {
        return new Thread(() -> {
            try {
                Thread.sleep(10000);
                if (u.state == UserState.Reconnecting) {
                    var lobby = lobbyManager.getLobbyByUid(u.uid);
                    if (lobby != null) {
                        lobby.onPlayerLeave((Player) u);
                        lobbyManager.remove(lobby);
                    }

                    userManager.remove(u);
                }
            } catch (InterruptedException ignored) { }
        });
    }
}

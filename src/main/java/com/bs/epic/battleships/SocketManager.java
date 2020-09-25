package com.bs.epic.battleships;

import com.bs.epic.battleships.documentation.Documentation;
import com.bs.epic.battleships.events.*;
import com.bs.epic.battleships.game.grid.GridPos;
import com.bs.epic.battleships.lobby.Lobby;
import com.bs.epic.battleships.lobby.LobbyManager;
import com.bs.epic.battleships.rest.service.AuthService;
import com.bs.epic.battleships.user.*;
import com.bs.epic.battleships.user.ai.AIPlayer;
import com.bs.epic.battleships.user.ai.behaviour.medium.AiState;
import com.bs.epic.battleships.user.player.Player;
import com.bs.epic.battleships.util.result.ShootSuccess;
import com.bs.epic.battleships.util.Util;
import com.bs.epic.battleships.verification.AuthValidator;
import com.bs.epic.battleships.verification.JwtUtil;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class SocketManager {
    SocketIOServer server;
    Configuration config;
    
    private final LobbyManager lobbyManager;
    private final UserManager userManager;

    private final JwtUtil jwtUtil;
    private final AuthService authService;

    AtomicInteger ids;

    public SocketManager(JwtUtil jwtUtil, AuthService authService) {
        ids = new AtomicInteger();

        lobbyManager = new LobbyManager();
        userManager = new UserManager();

        config = new Configuration();
        this.jwtUtil = jwtUtil;
        this.authService = authService;

        this.init();
    }

    public void init() {
        config.setHostname("0.0.0.0");
        config.setPort(6003);
        config.setContext("/sockets");

        server = new SocketIOServer(config);

        var documentation = Documentation.get();
        documentation.setSocketServer(server);

        server.addDisconnectListener((socket) -> {
            var user = userManager.getBySocket(socket);
            if (user == null) return;

            if (user.type == UserType.User) {
                userManager.remove(user);
                return;
            }

            var player = (Player) user;
            player.setReconnecting();

            var lobby = lobbyManager.getLobbyBySocket(socket);
            if (lobby == null) return;

            lobby.onPlayerDisconnect(player);
        });

        documentation.addEventListener("lastUid", Uid.class, Reconnect.class, (socket, data, ackRequest) -> {
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
        });

        documentation.addEventListener("inputUsername", Name.class, NameAccepted.class, (socket, data, ackRequest) -> {
            var result = AuthValidator.verifyUsername(data.name);
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
        });

        documentation.addEventListener("getLobbyInfo", Code.class, Name.class, (socket, data, ackRequest) -> {
            var user = userManager.getByCode(data.code);

            if (user == null) {
                socket.sendEvent("errorEvent", new ErrorEvent("getLobbyInfo", "User does not exist"));
                return;
            }

            socket.sendEvent("lobbyInfo", new Name(user.name));
        });

        documentation.addEventListener("tryCode", Code.class, LobbyJoined.class, (socket, data, ackRequest) -> {
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
        });

        documentation.addEventListener("singlePlayerSettings", Uid.class, State.class, (socket, data, ackRequest) -> {
            var player = userManager.getPlayer(data.uid);
            if (player == null) {
                socket.sendEvent("errorEvent", new ErrorEvent("singlePlayerSettings", "Invalid player."));
                return;
            }

            player.setState(UserState.Settings);
        });

        documentation.addEventListener("startSinglePlayerLobby", StartSinglePlayerLobby.class, LobbyJoined.class, (socket, data, ackRequest) -> {
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

            var opponent = new AIPlayer(data.time, data.difficulty);
            var lobby = new Lobby(ids.incrementAndGet(), player, opponent);
            opponent.lobby = lobby;
            lobbyManager.add(lobby);
            lobby.sendLobbyJoinedEvent();
        });

        documentation.addEventListener("leaveLobby", LeaveLobby.class, null, (socket, data, ackRequest) -> {
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
        });

        documentation.addEventListener("startSetup", LobbyId.class, null, (socket, data, ackRequest) -> {
            var lobby = lobbyManager.getLobby(data.id);
            if (lobby == null) {
                socket.sendEvent("errorEvent", new ErrorEvent("startGame", "Invalid lobby."));
                return;
            }

            lobby.initGame(10);
            lobby.sendEventToLobby("setupStarted");
        });

        documentation.addEventListener("placeShip", PlaceShip.class, null, (socket, data, ackRequest) -> {
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
        });

        documentation.addEventListener("autoPlaceShips", AutoPlaceShips.class, Ships.class, (socket, data, ackRequest) -> {
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
        });

        documentation.addEventListener("removeShip", RemoveShip.class, null, (socket, data, ackRequest) -> {
            var lobby = lobbyManager.getLobby(data.lobbyId);
            if (lobby == null) {
                socket.sendEvent("errorEvent", new ErrorEvent("removeShip", "Invalid lobby."));
                return;
            }

            lobby.game.removeShip(data.ship, userManager.getPlayer(data.uid));
            socket.sendEvent("removeShipAccepted");
        });

        documentation.addEventListener("submitSetup", DonePlacing.class, null, (socket, data, ackRequest) -> {
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
        });

        documentation.addEventListener("shoot", Shoot.class, ShootResult.class, (socket, data, ackRequest) -> {
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
        });

        documentation.addEventListener("getSetupData", Uid.class, SetupData.class, (socket, data, ackRequest) -> {
            var u = userManager.getUser(data.uid);
            if (u == null || u.type == UserType.User) return;

            var p = (Player) u;
            var lobby = lobbyManager.getLobbyByUid(p.uid);
            if (lobby == null) return;

            socket.sendEvent("setupData", new SetupData(
                lobby.id, p.name, lobby.getOtherPlayer(p).name, p.leader, p.getShips()
            ));
        });

        documentation.addEventListener("getNameData", Uid.class, NameData.class, (socket, data, ackRequest) -> {
            var u = userManager.getUser(data.uid);
            if (u == null || u.type == UserType.User) return;

            var p = (Player) u;
            socket.sendEvent("nameData", new NameData(p.code, p.name));
        });

        documentation.addEventListener("getGameData", Uid.class, GameData.class, (socket, data, ackRequest) -> {
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
        });

        documentation.addEventListener("sendMessage", Message.class, MessageReceived.class, (socket, data, ackRequest) -> {
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
        });

        documentation.addEventListener("getMessages", Uid.class, Messages.class, (socket, data, ackRequest) -> {
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
        });

        documentation.addEventListener("rematch", Rematch.class, null, (socket, data, ackRequest) -> {
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
        });

        documentation.addEventListener("loggedInUserWon", LoggedInUserWon.class, null, (socket, data, ackRequest) -> {
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
        });

        server.start();
    }

    public Thread getDisconnectThread(User u) {
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

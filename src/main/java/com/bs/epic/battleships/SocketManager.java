package com.bs.epic.battleships;

import com.bs.epic.battleships.events.*;
import com.bs.epic.battleships.lobby.Lobby;
import com.bs.epic.battleships.lobby.LobbyManager;
import com.bs.epic.battleships.user.*;
import com.bs.epic.battleships.util.result.ShootSuccess;
import com.bs.epic.battleships.util.Util;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class SocketManager {
    SocketIOServer server;
    Configuration config;
    
    private LobbyManager lobbyManager;
    private UserManager userManager;

    AtomicInteger ids;

    public SocketManager() {
        ids = new AtomicInteger();

        lobbyManager = new LobbyManager();
        userManager = new UserManager();

        config = new Configuration();
    }

    public void init() {
        config.setHostname("0.0.0.0");
        config.setPort(6003);
        config.setContext("/sockets");

        server = new SocketIOServer(config);

        server.addDisconnectListener((socket) -> {
            System.out.println(lobbyManager.lobbies.size());

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

        server.addEventListener("lastUid", String.class, (socket, uid, ackRequest) -> {
            var user = userManager.get(uid);
            if (user != null && user.type == UserType.Player) {
                var player = (Player) user;
                var lobby = lobbyManager.getLobbyByUid(uid);

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

        server.addEventListener("inputUsername", String.class, (socket, name, ackRequest) -> {
            var result = Util.verifyUsername(name);
            if (result.success) {
                if (userManager.nameExists(name)) {
                    socket.sendEvent("errorEvent", new ErrorEvent("inputUsername", "This username is already in use"));
                    return;
                }

                var player = new Player(name, socket, Util.generateNewCode(5));
                player.setThread(getDisconnectThread(player));
                userManager.replaceUserByPlayer(player);

                socket.sendEvent("nameAccepted", new NameAccepted(player.code, player.uid, player.name));
            }
            else {
                socket.sendEvent("errorEvent", result.getError());
            }
        });

        server.addEventListener("tryCode", String.class, (socket, code, ackRequest) -> {
            var cur = userManager.getBySocket(socket);
            var other = userManager.getByCode(code);

            if (cur == null) {
                socket.sendEvent("errorEvent", new ErrorEvent("tryCode", "Something went horribly wrong. Try refreshing the page."));
                return;
            }

            if (cur == other) {
                socket.sendEvent("errorEvent", new ErrorEvent("tryCode", "You can't enter your own lobby"));
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

        server.addEventListener("startGame", Integer.class, (socket, lobbyId, ackRequest) -> {
            var lobby = lobbyManager.getLobby(lobbyId);
            if (lobby == null) {
                socket.sendEvent("errorEvent", new ErrorEvent("startGame", "Invalid lobby"));
                return;
            }

            lobby.initGame(10);
            lobby.sendEventToLobby("gameStarted");
        });

        server.addEventListener("placeShip", PlaceShip.class, (socket, data, ackRequest) -> {
            var lobby = lobbyManager.getLobby(data.lobbyId);
            if (lobby == null) socket.sendEvent("errorEvent", new ErrorEvent("placeShip", "Invalid lobby"));

            var result = lobby.game.placeShip(lobby.getPlayer(data.uid), data.ship, data.i, data.j, data.horizontal);
            if (result.success) {
                socket.sendEvent("placeShipAccepted");
            }
            else {
                socket.sendEvent("errorEvent", result.getError());
            }
        });

        server.addEventListener("donePlacing", DonePlacing.class, (socket, data, ackRequest) -> {
            var lobby = lobbyManager.getLobby(data.lobbyId);
            if (lobby == null) {
                socket.sendEvent("errorEvent", new ErrorEvent("donePlacing", "Invalid lobby"));
                return;
            }

            var result = lobby.donePlacing(data.uid);
            if (result.success) {
                socket.sendEvent("donePlacingAccepted");
            }
            else {
                socket.sendEvent("errorEvent", result.getError());
            }
        });

        server.addEventListener("shoot", Shoot.class, (socket, data, ackRequest) -> {
            var lobby = lobbyManager.getLobby(data.lobbyId);
            if (lobby == null) {
                socket.sendEvent("errorEvent", new ErrorEvent("shoot", "Invalid lobby"));
                return;
            }

            var result = lobby.shoot(data.uid, data.i, data.j);
            if (result.success) {
                var suc = (ShootSuccess) result;
                socket.sendEvent("shotFired", suc.result);
            }
            else {
                socket.sendEvent("errorEvent", result.getError());
            }
        });

        server.start();
    }

    public Thread getDisconnectThread(User u) {
        return new Thread(() -> {
            try {
                Thread.sleep(1000000);
                if (u.state == UserState.Reconnecting) {
                    var lobby = lobbyManager.getLobbyByUid(u.uid);
                    if (lobby != null) {
                        lobby.onPlayerLeave((Player) u);
                        lobbyManager.remove(lobby);

                        System.out.println("BIG OEF");
                    }

                    userManager.remove(u);
                }
            } catch (InterruptedException ignored) { }
        });
    }
}

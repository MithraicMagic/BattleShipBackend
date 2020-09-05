package com.bs.epic.battleships;

import com.bs.epic.battleships.events.*;
import com.bs.epic.battleships.lobby.Lobby;
import com.bs.epic.battleships.lobby.LobbyManager;
import com.bs.epic.battleships.player.Player;
import com.bs.epic.battleships.player.PlayerState;
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
    
    LobbyManager lobbyManager;

    ArrayList<Player> availablePlayers;
    AtomicInteger ids;

    public SocketManager() {
        availablePlayers = new ArrayList<>();
        ids = new AtomicInteger();

        lobbyManager = new LobbyManager();
        config = new Configuration();
    }

    public void init() {
        config.setHostname("0.0.0.0");
        config.setPort(6003);
        config.setContext("/sockets");

        server = new SocketIOServer(config);

        server.addDisconnectListener((socket) -> {
            availablePlayers.removeIf(p -> p.socket == socket);
            var lobby = lobbyManager.getLobbyBySocket(socket);
            if (lobby == null) return;

            if (lobby.playerOne.socket == socket) {
                lobby.playerOne.setState(PlayerState.Reconnecting);
                lobby.playerTwo.socket.sendEvent("opponentReconnecting");
                lobby.disconnectThreadOne.start();
            } else if (lobby.playerTwo.socket == socket) {
                lobby.playerTwo.setState(PlayerState.Reconnecting);
                lobby.playerOne.socket.sendEvent("opponentReconnecting");
                lobby.disconnectThreadTwo.start();
            }
        });

        server.addEventListener("lastUid", String.class, (client, data, ackRequest) -> {
            var lobby = lobbyManager.getLobbyByUid(data);
            if (lobby == null) client.sendEvent("errorEvent", new ErrorEvent("lastUid", "Invalid lobby"));

            if (lobby.playerOne.UID.equals(data)) {
                lobby.playerOne.socket = client;

                var prevState = lobby.playerOne.prevState;
                var state = prevState.ordinal();

                if (prevState == PlayerState.YourTurn) state = 1;
                if (prevState == PlayerState.OpponentTurn) state = 2;

                client.sendEvent("reconnect",
                    new Reconnect(lobby.playerOne.name, lobby.playerTwo.name, true, lobby.id, state)
                );
                lobby.playerTwo.socket.sendEvent("opponentReconnected");

                lobby.playerOne.revertState();
                lobby.disconnectThreadOne.interrupt();
                lobby.disconnectThreadOne = getDisconnectThread(lobby);
            } else if (lobby.playerTwo.UID.equals(data)) {
                lobby.playerTwo.socket = client;

                var prevState = lobby.playerTwo.prevState;
                var state = prevState.ordinal();

                if (prevState == PlayerState.YourTurn) state = 1;
                if (prevState == PlayerState.OpponentTurn) state = 2;

                client.sendEvent("reconnect",
                    new Reconnect(lobby.playerTwo.name, lobby.playerOne.name, false, lobby.id, state)
                );
                lobby.playerOne.socket.sendEvent("opponentReconnected");

                lobby.playerTwo.revertState();
                lobby.disconnectThreadTwo.interrupt();
                lobby.disconnectThreadTwo = getDisconnectThread(lobby);
            }
        });

        server.addEventListener("inputUsername", String.class, (client, data, ackRequest) -> {
            var result = Util.verifyUsername(data);
            if (result.success) {
                for (Player p : availablePlayers) {
                    if (p.name.equals(data)) {
                        client.sendEvent("errorEvent", new ErrorEvent("inputUsername", "This username is already in use"));
                        return;
                    }
                }

                Player player = new Player(data, client, Util.generateNewCode(5));
                player.setState(PlayerState.Available);

                System.out.println("New player (" + data + ") created, with code: " + player.code);

                client.sendEvent("nameAccepted", new NameAccepted(player.code, player.UID, player.name));
                availablePlayers.add(player);
            }
            else {
                client.sendEvent("errorEvent", result.getError());
            }
        });

        server.addEventListener("tryCode", String.class, (client, data, ackRequest) -> {
            var success = false;
            Player current = null;
            Player other = null;

            for (Player p : availablePlayers) {
                if (p.socket == client) {
                    current = p;
                    continue;
                }
                if (p.code.equals(data)) {
                    other = p;
                    success = true;
                }
            }

            if (success && current != null) {
                Lobby lobby = new Lobby(ids.incrementAndGet(), other, current);
                lobby.addThread(getDisconnectThread(lobby), getDisconnectThread(lobby));

                current.setState(PlayerState.Lobby);
                current.setState(PlayerState.Lobby);

                availablePlayers.remove(current);
                availablePlayers.remove(other);

                lobbyManager.add(lobby);
                lobby.sendLobbyJoinedEvent();
            } else {
                client.sendEvent("errorEvent", new ErrorEvent("tryCode", "You did not enter a valid code!"));
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

    public Thread getDisconnectThread(Lobby l) {
        return new Thread(() -> {
            try {
                Thread.sleep(10000);
                if (l.playerOne.state == PlayerState.Reconnecting) {
                    availablePlayers.add(l.playerTwo);
                    l.playerTwo.socket.sendEvent("opponentLeft");
                }
                else {
                    availablePlayers.add(l.playerOne);
                    l.playerOne.socket.sendEvent("opponentLeft");
                }

                lobbyManager.remove(l);
            } catch (InterruptedException ignored) { }
        });
    }
}

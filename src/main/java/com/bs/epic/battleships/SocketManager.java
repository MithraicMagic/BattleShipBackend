package com.bs.epic.battleships;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class SocketManager {
    SocketIOServer server;
    Configuration config;

    ArrayList<Player> availablePlayers;
    ArrayList<Lobby> lobbies;
    AtomicInteger ids;

    public SocketManager() {
        availablePlayers = new ArrayList<>();
        lobbies = new ArrayList<>();
        ids = new AtomicInteger();

        config = new Configuration();
    }

    public void init() {
        config.setHostname("0.0.0.0");
        config.setPort(6003);
        config.setContext("/sockets");

        server = new SocketIOServer(config);

        server.addDisconnectListener((socket) -> {
            availablePlayers.removeIf(p -> p.socket == socket);

            for (Lobby l : lobbies) {
                if (l.playerOne.socket == socket) {
                    l.sendEventToLobby("message", new Message("Other player is reconnecting!"));
                    l.disconnectThreadOne.start();
                } else if (l.playerTwo.socket == socket) {
                    l.sendEventToLobby("message", new Message("Other player is reconnecting!"));
                    l.disconnectThreadTwo.start();
                }
            }
        });

        server.addEventListener("lastUid", String.class, (client, data, ackRequest) -> {
            for (Lobby l : lobbies) {
                if (l.playerOne.UID.equals(data)) {
                    l.playerOne.socket = client;
                    client.sendEvent("lobbyId", new Message(l.Id));
                    client.sendEvent("myUsername", new Message(l.playerOne.name));
                    client.sendEvent("otherUsername", new Message(l.playerTwo.name));
                    l.disconnectThreadOne.interrupt();
                    l.disconnectThreadOne = getDisconnectThread(l);
                } else if (l.playerTwo.UID.equals(data)) {
                    l.playerTwo.socket = client;
                    client.sendEvent("lobbyId", new Message(l.Id));
                    client.sendEvent("myUsername", new Message(l.playerTwo.name));
                    client.sendEvent("otherUsername", new Message(l.playerOne.name));
                    l.disconnectThreadTwo.interrupt();
                    l.disconnectThreadTwo = getDisconnectThread(l);
                }
            }
        });

        server.addEventListener("inputUsername", String.class, (client, data, ackRequest) -> {
            Player player = new Player(data, client, generateNewCode(5));
            System.out.println("New player (" + data + ") created, with code: " + player.code);
            client.sendEvent("playerCode", new Message(player.code));
            client.sendEvent("newUid", new Message(player.UID));
            availablePlayers.add(player);
        });

        server.addEventListener("tryCode", String.class, (client, data, ackRequest) -> {
            boolean success = false;
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
                Lobby lobby = new Lobby(ids.incrementAndGet(), current, other);
                lobby.addThread(getDisconnectThread(lobby), getDisconnectThread(lobby));

                availablePlayers.remove(current);
                availablePlayers.remove(other);

                lobbies.add(lobby);
                lobby.sendEventToLobby("lobbyId", new Message(lobby.Id));
                current.socket.sendEvent("otherUsername", new Message(other.name));
                other.socket.sendEvent("otherUsername", new Message(current.name));
            } else {
                client.sendEvent("message", new Message("You did not enter a valid code!"));
            }
        });

        server.start();
    }

    public String generateNewCode(int length) {
        int leftLimit = 48; // letter 'a'
        int rightLimit = 122; // letter 'z'
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    public Thread getDisconnectThread(Lobby l) {
        return new Thread(() -> {
            try {
                Thread.sleep(10000);
                lobbies.remove(l);
            } catch (InterruptedException ignored) { }
        });
    }
}

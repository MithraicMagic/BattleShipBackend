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
    ArrayList<Player> availablePlayers = new ArrayList<>();
    ArrayList<Lobby> lobbies = new ArrayList<>();
    AtomicInteger Ids = new AtomicInteger();

    public void init() {
        Configuration config = new Configuration();
        config.setHostname("0.0.0.0");
        config.setPort(6003);
        config.setContext("/sockets");

        server = new SocketIOServer(config);

        server.addDisconnectListener((socket) -> availablePlayers.removeIf(p -> p.socket == socket));

        server.addEventListener("inputUsername", String.class, (client, data, ackRequest) -> {
            Player player = new Player(data, client, generateNewPlayerCode());
            System.out.println("New player (" + data + ") created, with code: " + player.code);
            client.sendEvent("playerCode", new Message(player.code));
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
                Lobby lobby = new Lobby(Ids.incrementAndGet(), current, other);
                lobbies.add(lobby);
                lobby.sendEventToLobby("lobbyId", new Message(lobby.Id));
                current.socket.sendEvent("otherUsername", new Message(other.name));
                other.socket.sendEvent("otherUsername", new Message(current.name));
                availablePlayers.remove(current);
                availablePlayers.remove(other);
            } else {
                client.sendEvent("message", new Message("You did not enter a valid code!"));
            }
        });

        server.start();
    }

    public String generateNewPlayerCode() {
        int leftLimit = 48; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 5;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}

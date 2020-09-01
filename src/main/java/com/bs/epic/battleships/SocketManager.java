package com.bs.epic.battleships;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class SocketManager {
    SocketIOServer server;
    ArrayList<Lobby> lobbies = new ArrayList<>();
    AtomicInteger Ids = new AtomicInteger();

    public void init() {
        Configuration config = new Configuration();
        config.setHostname("0.0.0.0");
        config.setPort(6003);
        config.setContext("/sockets");

        server = new SocketIOServer(config);
        server.addConnectListener((socket) -> {
            Lobby lobby = new Lobby(Ids.incrementAndGet(), createRandomLobbyCode(), socket);
            System.out.println("New lobby created for player, with code: " + lobby.code);
            socket.sendEvent("lobbyCode", new Message(lobby.code));
            socket.sendEvent("lobbyId", new Message(lobby.Id));
            lobbies.add(lobby);
        });

        server.addEventListener("tryCode", String.class, (client, data, ackRequest) -> {
            boolean success = false;

            for (Lobby l : lobbies) {
                if (l.code.equals(data) && l.playerOne.socket != client) {
                    l.addPlayer(client);
                    l.sendEventToLobby("message", new Message("Successful connection!"));
                    success = true;
                }
            }

            if (!success) {
                client.sendEvent("message", new Message("NOPE!"));
            }
        });

        server.start();
    }

    public String createRandomLobbyCode() {
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

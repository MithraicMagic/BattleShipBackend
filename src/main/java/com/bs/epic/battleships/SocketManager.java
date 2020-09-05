package com.bs.epic.battleships;

import com.bs.epic.battleships.events.ErrorEvent;
import com.bs.epic.battleships.events.NameAccepted;
import com.bs.epic.battleships.events.Reconnect;
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

                    client.sendEvent("reconnect", new Reconnect(l.playerOne.name, l.playerTwo.name, l.id));
                    l.disconnectThreadOne.interrupt();
                    l.disconnectThreadOne = getDisconnectThread(l);
                } else if (l.playerTwo.UID.equals(data)) {
                    l.playerTwo.socket = client;

                    client.sendEvent("reconnect", new Reconnect(l.playerTwo.name, l.playerOne.name, l.id));
                    l.disconnectThreadTwo.interrupt();
                    l.disconnectThreadTwo = getDisconnectThread(l);
                }
            }
        });

        server.addEventListener("inputUsername", String.class, (client, data, ackRequest) -> {
            for (Player p : availablePlayers) {
                if (p.name.equals(data)) {
                    client.sendEvent("errorEvent", new ErrorEvent("inputUsername", "This username is already in use"));
                    return;
                }
            }

            Player player = new Player(data, client, Util.generateNewCode(5));
            System.out.println("New player (" + data + ") created, with code: " + player.code);

            client.sendEvent("nameAccepted", new NameAccepted(player.code, player.UID, player.name));
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
                lobby.sendLobbyJoinedEvent();
            } else {
                client.sendEvent("errorEvent", new ErrorEvent("tryCode", "You did not enter a valid code!"));
            }
        });

        server.start();
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

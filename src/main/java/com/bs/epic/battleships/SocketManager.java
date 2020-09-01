package com.bs.epic.battleships;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

@Component
public class SocketManager {
    SocketIOServer server;

    public void init() {
        Configuration config = new Configuration();
        config.setHostname("localhost");
        config.setPort(6003);
        config.setOrigin("*");

        server = new SocketIOServer(config);
        server.addEventListener("chatevent", ChatObject.class, (client, data, ackRequest) -> {
            // broadcast messages to all clients
            System.out.println(data);
            server.getBroadcastOperations().sendEvent("chatevent", data);
        });

        server.start();
    }
}

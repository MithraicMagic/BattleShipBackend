package com.bs.epic.battleships;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class })
public class BattleshipsApplication {

    public static void main(String[] args) {

        System.setProperty("server.servlet.context-path", "/api");
        SpringApplication.run(BattleshipsApplication.class, args);
        SocketManager chat = new SocketManager();
        chat.init();
    }

}

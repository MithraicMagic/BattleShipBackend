package com.bs.epic.battleships;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.Socket;

@SpringBootApplication
public class BattleshipsApplication {

    public static void main(String[] args) {
        SpringApplication.run(BattleshipsApplication.class, args);
        SocketManager chat = new SocketManager();
        chat.init();
    }

}

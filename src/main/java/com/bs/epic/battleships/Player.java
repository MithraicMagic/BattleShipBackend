package com.bs.epic.battleships;

import com.corundumstudio.socketio.SocketIOClient;

import java.util.UUID;

public class Player {
    public String name;
    public SocketIOClient socket;
    public String code;
    public String UID;

    public Player(String name, SocketIOClient socket, String code) {
        this.name = name;
        this.socket = socket;
        this.code = code;
        this.UID = UUID.randomUUID().toString();
    }
}

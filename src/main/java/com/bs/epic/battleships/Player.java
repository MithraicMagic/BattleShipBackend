package com.bs.epic.battleships;

import com.corundumstudio.socketio.SocketIOClient;

public class Player {
    public String name;
    public SocketIOClient socket;
    public String code;

    public Player(String name, SocketIOClient socket, String code) {
        this.name = name;
        this.socket = socket;
        this.code = code;
    }
}

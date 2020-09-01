package com.bs.epic.battleships;

import com.corundumstudio.socketio.SocketIOClient;

public class Player {
    public String name;
    public SocketIOClient socket;

    public Player(String name, SocketIOClient socket) {
        this.name = name;
        this.socket = socket;
    }
}

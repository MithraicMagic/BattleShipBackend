package com.bs.epic.battleships;

import com.corundumstudio.socketio.SocketIOClient;

public class Player {
    public String name;
    public SocketIOClient socket;
    public String code;
    public String UID;

    public Player(String name, SocketIOClient socket, String code, String UID) {
        this.name = name;
        this.socket = socket;
        this.code = code;
        this.UID = UID;
    }
}

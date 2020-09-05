package com.bs.epic.battleships.player;

import com.bs.epic.battleships.game.GridCell;
import com.bs.epic.battleships.game.Ship;
import com.corundumstudio.socketio.SocketIOClient;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

public class Player {
    public String name;
    public SocketIOClient socket;
    public String code;
    public String UID;

    public PlayerState state;
    public PlayerState prevState;

    public boolean donePlacing;

    public ArrayList<GridCell> cells;
    public Map<String, Ship> ships;

    public Player(String name, SocketIOClient socket, String code) {
        this.name = name;
        this.socket = socket;
        this.code = code;
        this.UID = UUID.randomUUID().toString();
        this.state = PlayerState.Available;
        this.prevState = PlayerState.Available;
        this.donePlacing = false;
    }

    public boolean isEqual(String uid) {
        return UID.equals(uid);
    }

    public void setState(PlayerState state) {
        prevState = this.state;
        this.state = state;

        if (state != PlayerState.Reconnecting) socket.sendEvent("playerState", this.state);
    }

    public void revertState() {
        var temp = prevState;
        prevState = state;
        state = temp;
    }
}

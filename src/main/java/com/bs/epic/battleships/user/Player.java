package com.bs.epic.battleships.user;

import com.bs.epic.battleships.game.GridCell;
import com.bs.epic.battleships.game.Ship;
import com.corundumstudio.socketio.SocketIOClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

public class Player extends User {
    public String name;
    public String code;

    public boolean donePlacing;
    public boolean leader;

    public ArrayList<GridCell> cells;
    public Map<String, Ship> ships;

    public Player(String name, SocketIOClient socket, String code) {
        super(socket);

        this.type = UserType.Player;
        this.name = name;
        this.code = code;
        this.donePlacing = false;
        this.leader = false;

        this.setState(UserState.Available);
    }

    public void setReconnecting() {
        this.setState(UserState.Reconnecting);
        disconnectThread.start();
    }

    public void onReconnect(SocketIOClient socket) {
        this.socket = socket;
        this.revertState();
        disconnectThread.interrupt();
    }

    public void setThread(Thread t) {
        disconnectThread = t;
    }

    public Collection<Ship> getShips() {
        return ships.values();
    }
}

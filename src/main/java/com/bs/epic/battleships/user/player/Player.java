package com.bs.epic.battleships.user.player;

import com.bs.epic.battleships.game.grid.Grid;
import com.bs.epic.battleships.game.grid.GridPos;
import com.bs.epic.battleships.game.Ship;
import com.bs.epic.battleships.user.User;
import com.bs.epic.battleships.user.UserState;
import com.bs.epic.battleships.user.UserType;
import com.corundumstudio.socketio.SocketIOClient;

import java.util.*;

public class Player extends User {
    public String name;
    public String code;

    public boolean leader;

    public Grid grid;
    public Map<String, Ship> ships;

    public ArrayList<GridPos> hits;
    public ArrayList<GridPos> misses;

    public int boatsLeft;

    public Player(String name, SocketIOClient socket, String code) {
        super(socket, UserType.Player);
        init(name, code);
    }

    public Player(String name, SocketIOClient socket, String code, UserType type) {
        super(socket, type);
        init(name, code);
    }

    private void init(String name, String code) {
        this.name = name;
        this.code = code;
        this.leader = false;

        this.grid = new Grid();
        this.hits = new ArrayList<>();
        this.misses = new ArrayList<>();

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

    public void onLobbyRemoved() {
        if (grid != null) grid.clear();
        if (ships != null) ships.clear();

        if (hits != null) hits.clear();
        if (misses != null) misses.clear();

        boatsLeft = 5;
    }

    public void setThread(Thread t) {
        disconnectThread = t;
    }

    public Collection<Ship> getShips() {
        return ships.values();
    }
}

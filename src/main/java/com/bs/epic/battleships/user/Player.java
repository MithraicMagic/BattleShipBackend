package com.bs.epic.battleships.user;

import com.bs.epic.battleships.events.Messages;
import com.bs.epic.battleships.game.GridCell;
import com.bs.epic.battleships.game.GridPos;
import com.bs.epic.battleships.game.Ship;
import com.corundumstudio.socketio.SocketIOClient;

import java.util.*;

public class Player extends User {
    public String name;
    public String code;

    public boolean donePlacing;
    public boolean leader;

    private Deque<String> sentMessages;
    private Deque<String> receivedMessages;

    public ArrayList<GridCell> cells;
    public Map<String, Ship> ships;

    public ArrayList<GridPos> hits;
    public ArrayList<GridPos> misses;

    public Player(String name, SocketIOClient socket, String code) {
        super(socket);

        this.type = UserType.Player;
        this.name = name;
        this.code = code;
        this.donePlacing = false;
        this.leader = false;

        this.hits = new ArrayList<>();
        this.misses = new ArrayList<>();

        this.sentMessages = new ArrayDeque<>();
        this.receivedMessages = new ArrayDeque<>();

        this.setState(UserState.Available);
    }

    public void sendMessage(Player receiver, String message) {
        sentMessages.addLast(message);
        if (sentMessages.size() > 10) {
            sentMessages.removeFirst();
        }
        receiver.receiveMessage(message);
    }

    public void receiveMessage(String message) {
        receivedMessages.addLast(message);
        if (receivedMessages.size() > 10) {
            receivedMessages.removeFirst();
        }

        socket.sendEvent("messageReceived", message);
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

    public Messages getMessages() { return new Messages(sentMessages, receivedMessages); }
}

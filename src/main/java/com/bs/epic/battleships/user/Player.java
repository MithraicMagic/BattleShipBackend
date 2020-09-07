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

    public boolean leader;

    private Deque<PlayerMessage> sentMessages;
    private Deque<PlayerMessage> receivedMessages;

    public ArrayList<GridCell> cells;
    public Map<String, Ship> ships;

    public ArrayList<GridPos> hits;
    public ArrayList<GridPos> misses;

    public Player(String name, SocketIOClient socket, String code) {
        super(socket);

        this.type = UserType.Player;
        this.name = name;
        this.code = code;
        this.leader = false;

        this.hits = new ArrayList<>();
        this.misses = new ArrayList<>();

        this.sentMessages = new ArrayDeque<>();
        this.receivedMessages = new ArrayDeque<>();

        this.setState(UserState.Available);
    }

    public void sendMessage(Player receiver, String message) {
        sentMessages.addLast(new PlayerMessage(message));
        if (sentMessages.size() > 10) {
            sentMessages.removeFirst();
        }
        receiver.receiveMessage(message);
    }

    public void receiveMessage(String message) {
        receivedMessages.addLast(new PlayerMessage(message));
        if (receivedMessages.size() > 10) {
            receivedMessages.removeFirst();
        }

        socket.sendEvent("messageReceived", new PlayerMessage(message));
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
        sentMessages.clear();
        receivedMessages.clear();

        if (cells != null) cells.clear();
        if (ships != null) ships.clear();

        if (hits != null) hits.clear();
        if (misses != null) misses.clear();
    }

    public void setThread(Thread t) {
        disconnectThread = t;
    }

    public Collection<Ship> getShips() {
        return ships.values();
    }

    public Messages getMessages() { return new Messages(sentMessages, receivedMessages); }
}

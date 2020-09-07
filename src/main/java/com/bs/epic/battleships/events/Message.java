package com.bs.epic.battleships.events;

public class Message {
    public int lobbyId;

    public String uid;
    public String message;

    public Message() {}

    public Message(int lobbyId, String uid, String message) {
        this.lobbyId = lobbyId;
        this.uid = uid;
        this.message = message;
    }
}

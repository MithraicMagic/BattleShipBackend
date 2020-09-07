package com.bs.epic.battleships.events;

public class Message {
    public String uid;
    public String message;

    public Message() {}

    public Message(String uid, String message) {
        this.uid = uid;
        this.message = message;
    }
}

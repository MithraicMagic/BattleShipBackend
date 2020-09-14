package com.bs.epic.battleships.events;

import com.bs.epic.battleships.documentation.annotations.Doc;

public class Message {
    @Doc("The unique id for the player")
    public String uid;
    @Doc("The message")
    public String message;

    public Message() {}

    public Message(String uid, String message) {
        this.uid = uid;
        this.message = message;
    }
}

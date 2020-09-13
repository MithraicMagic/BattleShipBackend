package com.bs.epic.battleships.events;

import com.bs.epic.battleships.documentation.Doc;

public class Message {
    @Doc(description = "The unique id for the player")
    public String uid;
    @Doc(description = "The message")
    public String message;

    public Message() {}

    public Message(String uid, String message) {
        this.uid = uid;
        this.message = message;
    }
}

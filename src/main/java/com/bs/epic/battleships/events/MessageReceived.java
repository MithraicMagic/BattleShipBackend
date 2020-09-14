package com.bs.epic.battleships.events;

import com.bs.epic.battleships.documentation.annotations.Doc;

public class MessageReceived {
    @Doc("The message that the player received")
    public String message;

    public MessageReceived(String message) {
        this.message = message;
    }
}

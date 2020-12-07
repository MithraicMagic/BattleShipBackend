package com.bs.epic.battleships.rest.requestbodies;

import com.bs.epic.battleships.documentation.annotations.Doc;

public class AddMessage {
    @Doc("The text that makes up the message")
    public String text;

    public AddMessage() {}

    public AddMessage(String text) {
        this.text = text;
    }
}

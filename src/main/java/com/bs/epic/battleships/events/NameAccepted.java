package com.bs.epic.battleships.events;

import com.bs.epic.battleships.documentation.Doc;

public class NameAccepted {
    @Doc(description = "The player's code which others can use to join his/her lobby")
    public String code;
    @Doc(description = "The unique id for the player")
    public String uid;
    @Doc(description = "The player's chosen name")
    public String name;

    public NameAccepted(String code, String uid, String name) {
        this.code = code;
        this.uid = uid;
        this.name = name;
    }
}

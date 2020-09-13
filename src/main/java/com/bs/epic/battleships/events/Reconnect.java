package com.bs.epic.battleships.events;

import com.bs.epic.battleships.documentation.Doc;

public class Reconnect {
    @Doc(description = "The player's name")
    public String me;
    @Doc(description = "The player's code which other user can use to join his/her lobby")
    public String code;

    public Reconnect(String me, String code) {
        this.me = me;
        this.code = code;
    }
}

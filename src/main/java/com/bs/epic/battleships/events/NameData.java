package com.bs.epic.battleships.events;

import com.bs.epic.battleships.documentation.annotations.Doc;

public class NameData {
    @Doc("The player's code which others can use to join his/her lobby")
    public String code;
    @Doc("The player's chosen name")
    public String me;

    public NameData(String code, String me) {
        this.code = code;
        this.me = me;
    }
}

package com.bs.epic.battleships.events;

import com.bs.epic.battleships.documentation.annotations.Doc;

public class Code {
    @Doc("A user's code to join his/her lobby")
    public String code;

    public Code(String code) {
        this.code = code;
    }

    public Code() {}
}

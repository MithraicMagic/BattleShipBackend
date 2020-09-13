package com.bs.epic.battleships.events;

import com.bs.epic.battleships.documentation.Doc;

public class Name {
    @Doc(description = "Player's name")
    public String name;

    public Name(String name) {
        this.name = name;
    }
}

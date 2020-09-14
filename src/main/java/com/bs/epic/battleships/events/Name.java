package com.bs.epic.battleships.events;

import com.bs.epic.battleships.documentation.annotations.Doc;

public class Name {
    @Doc("Player's name")
    public String name;

    public Name(String name) {
        this.name = name;
    }
}

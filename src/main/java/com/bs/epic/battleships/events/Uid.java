package com.bs.epic.battleships.events;
import com.bs.epic.battleships.documentation.annotations.Doc;

public class Uid {
    @Doc("The unique id for the player")
    public String uid;

    public Uid() {}

    public Uid(String uid) {
        this.uid = uid;
    }
}

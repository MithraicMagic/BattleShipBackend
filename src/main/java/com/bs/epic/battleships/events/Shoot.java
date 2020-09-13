package com.bs.epic.battleships.events;

import com.bs.epic.battleships.documentation.Doc;

public class Shoot {
    @Doc(description = "The unique id for the player")
    public String uid;
    @Doc(description = "The unique id for the lobby")
    public int lobbyId;
    @Doc(description = "The coordinates at which the player wants to shoot")
    public int i, j;

    public Shoot() {}

    public Shoot(String uid, int lobbyId, int i, int j) {
        this.uid = uid;
        this.lobbyId = lobbyId;

        this.i = i;
        this.j = j;
    }
}

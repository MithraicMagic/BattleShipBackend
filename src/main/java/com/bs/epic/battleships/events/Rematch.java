package com.bs.epic.battleships.events;

import com.bs.epic.battleships.documentation.Doc;

public class Rematch {
    @Doc(description = "The unique id for the lobby")
    public int lobbyId;
    @Doc(description = "The unique id for the player")
    public String uid;

    public Rematch() {}

    public Rematch(int lobbyId, String uid) {
        this.lobbyId = lobbyId;
        this.uid = uid;
    }
}

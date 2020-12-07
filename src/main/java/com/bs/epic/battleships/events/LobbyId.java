package com.bs.epic.battleships.events;

import com.bs.epic.battleships.documentation.annotations.Doc;

public class LobbyId {
    @Doc("The unique id for the lobby")
    public int id;

    public LobbyId() {}

    public LobbyId(int id) {
        this.id = id;
    }
}

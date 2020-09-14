package com.bs.epic.battleships.events;

import com.bs.epic.battleships.documentation.Doc;

public class LeaveLobby {
    @Doc("The unique id for the player")
    public String uid;
    @Doc("The unique id for the lobby")
    public int lobbyId;

    public LeaveLobby() {}

    public LeaveLobby(String uid, int lobbyId) {
        this.uid = uid;
        this.lobbyId = lobbyId;
    }
}

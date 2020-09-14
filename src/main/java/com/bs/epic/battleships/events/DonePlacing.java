package com.bs.epic.battleships.events;

import com.bs.epic.battleships.documentation.Doc;

public class DonePlacing {
    @Doc("The unique id for the lobby")
    public int lobbyId;
    @Doc("The unique id for the player")
    public String uid;

    public DonePlacing() {}

    public DonePlacing(int lobbyId, String uid) {
        this.lobbyId = lobbyId;
        this.uid = uid;
    }
}

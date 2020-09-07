package com.bs.epic.battleships.events;

public class DonePlacing {
    public int lobbyId;
    public String uid;

    public DonePlacing() {}

    public DonePlacing(int lobbyId, String uid) {
        this.lobbyId = lobbyId;
        this.uid = uid;
    }
}

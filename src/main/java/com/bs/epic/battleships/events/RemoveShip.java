package com.bs.epic.battleships.events;

public class RemoveShip {
    public int lobbyId;
    public String ship;
    public String uid;

    public RemoveShip() {}

    public RemoveShip(int lobbyId, String ship, String uid) {
        this.lobbyId = lobbyId;
        this.ship = ship;
        this.uid = uid;
    }
}

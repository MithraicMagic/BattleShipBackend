package com.bs.epic.battleships.events;

public class RemoveShip {
    public int lobbyId;
    public String name;
    public String uid;

    public RemoveShip() {}

    public RemoveShip(int lobbyId, String name, String uid) {
        this.lobbyId = lobbyId;
        this.name = name;
        this.uid = uid;
    }
}

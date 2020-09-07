package com.bs.epic.battleships.events;

public class AutoPlaceShips {
    public int lobbyId;
    public String uid;

    public AutoPlaceShips() {}

    public AutoPlaceShips(int lobbyId, String uid) {
        this.lobbyId = lobbyId;
        this.uid = uid;
    }
}

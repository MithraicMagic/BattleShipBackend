package com.bs.epic.battleships.events;

public class Rematch {
    public int lobbyId;
    public String uid;

    public Rematch() {}

    public Rematch(int lobbyId, String uid) {
        this.lobbyId = lobbyId;
        this.uid = uid;
    }
}

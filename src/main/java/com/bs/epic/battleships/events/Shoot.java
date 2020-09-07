package com.bs.epic.battleships.events;

public class Shoot {
    public String uid;
    public int lobbyId;
    public int i, j;

    public Shoot() {}

    public Shoot(String uid, int lobbyId, int i, int j) {
        this.uid = uid;
        this.lobbyId = lobbyId;

        this.i = i;
        this.j = j;
    }
}

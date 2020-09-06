package com.bs.epic.battleships.events;

public class LeaveLobby {
    public String uid;
    public int lobbyId;

    public LeaveLobby() {}

    public LeaveLobby(String uid, int lobbyId) {
        this.uid = uid;
        this.lobbyId = lobbyId;
    }
}

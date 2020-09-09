package com.bs.epic.battleships.events;

public class StartSinglePlayerLobby {
    public String uid;
    public int difficulty;
    public int time;

    public StartSinglePlayerLobby() {}

    public StartSinglePlayerLobby(String uid, int difficulty, int time) {
        this.uid = uid;
        this.difficulty = difficulty;
        this.time = time;
    }
}

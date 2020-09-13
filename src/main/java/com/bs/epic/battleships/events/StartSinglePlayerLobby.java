package com.bs.epic.battleships.events;

import com.bs.epic.battleships.documentation.Doc;

public class StartSinglePlayerLobby {
    @Doc(description = "The unique id for the player")
    public String uid;
    @Doc(description = "The difficulty level that the player wants to play at")
    public int difficulty;
    @Doc(description = "How much time (in ms) the computer should wait before deciding where to shoot")
    public int time;

    public StartSinglePlayerLobby() {}

    public StartSinglePlayerLobby(String uid, int difficulty, int time) {
        this.uid = uid;
        this.difficulty = difficulty;
        this.time = time;
    }
}

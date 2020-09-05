package com.bs.epic.battleships.events;

public class LobbyJoined {
    public int id;
    public String otherName;

    public LobbyJoined(int id, String otherName) {
        this.id = id;
        this.otherName = otherName;
    }
}

package com.bs.epic.battleships.events;

public class LobbyJoined {
    public int id;
    public String otherName;
    public boolean leader;

    public LobbyJoined(int id, String otherName, boolean leader) {
        this.id = id;
        this.otherName = otherName;
        this.leader = leader;
    }
}

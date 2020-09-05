package com.bs.epic.battleships.events;

public class Reconnect {
    public String me;
    public String opponent;
    public int lobbyId;

    public Reconnect(String me, String opponent, int lobbyId) {
        this.me = me;
        this.opponent = opponent;
        this.lobbyId = lobbyId;
    }
}

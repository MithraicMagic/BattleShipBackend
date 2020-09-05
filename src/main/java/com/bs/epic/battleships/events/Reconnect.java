package com.bs.epic.battleships.events;

public class Reconnect {
    public String me;
    public String opponent;
    public boolean leader;
    public int lobbyId;
    public int gameState;

    public Reconnect(String me, String opponent, boolean leader, int lobbyId, int gameState) {
        this.me = me;
        this.opponent = opponent;
        this.leader = leader;
        this.lobbyId = lobbyId;
        this.gameState = gameState;
    }
}

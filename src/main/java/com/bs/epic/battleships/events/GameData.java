package com.bs.epic.battleships.events;

public class GameData {
    public int lobbyId;
    public String me;
    public String opponent;
    public boolean leader;

    public GameData(int lobbyId, String me, String opponent, boolean leader) {
        this.lobbyId = lobbyId;
        this.me = me;
        this.opponent = opponent;
        this.leader = leader;
    }
}
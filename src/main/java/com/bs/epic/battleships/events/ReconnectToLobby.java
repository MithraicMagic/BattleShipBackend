package com.bs.epic.battleships.events;

public class ReconnectToLobby {
    public String me;
    public String opponent;
    public boolean leader;
    public int lobbyId;

    public ReconnectToLobby(String me, String opponent, boolean leader, int lobbyId) {
        this.me = me;
        this.opponent = opponent;
        this.leader = leader;
        this.lobbyId = lobbyId;
    }
}

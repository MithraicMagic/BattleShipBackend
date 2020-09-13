package com.bs.epic.battleships.events;

import com.bs.epic.battleships.documentation.Doc;

public class ReconnectToLobby {
    @Doc(description = "The player's name")
    public String me;
    @Doc(description = "The opponent's name")
    public String opponent;
    @Doc(description = "Boolean indicating if the player is the lobby's leader")
    public boolean leader;
    @Doc(description = "The unique id for the lobby")
    public int lobbyId;

    public ReconnectToLobby(String me, String opponent, boolean leader, int lobbyId) {
        this.me = me;
        this.opponent = opponent;
        this.leader = leader;
        this.lobbyId = lobbyId;
    }
}

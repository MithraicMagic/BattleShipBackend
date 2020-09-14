package com.bs.epic.battleships.events;

import com.bs.epic.battleships.documentation.annotations.Doc;

public class ReconnectToLobby {
    @Doc("The player's name")
    public String me;
    @Doc("The opponent's name")
    public String opponent;
    @Doc("Boolean indicating if the player is the lobby's leader")
    public boolean leader;
    @Doc("The unique id for the lobby")
    public int lobbyId;

    public ReconnectToLobby(String me, String opponent, boolean leader, int lobbyId) {
        this.me = me;
        this.opponent = opponent;
        this.leader = leader;
        this.lobbyId = lobbyId;
    }
}

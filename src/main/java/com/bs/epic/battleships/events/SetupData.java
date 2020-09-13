package com.bs.epic.battleships.events;

import com.bs.epic.battleships.documentation.Doc;
import com.bs.epic.battleships.game.Ship;

import java.util.ArrayList;
import java.util.Collection;

public class SetupData {
    @Doc(description = "The unique id for the lobby")
    public int lobbyId;
    @Doc(description = "The player's name")
    public String me;
    @Doc(description = "The opponent's name")
    public String opponent;
    @Doc(description = "Boolean indicating if the player is the lobby's leader")
    public boolean leader;

    @Doc(description = "An array containing all the boats of the player")
    public Collection<Ship> boatData;

    public SetupData(int lobbyId, String me, String opponent, boolean leader, Collection<Ship> boatData) {
        this.lobbyId = lobbyId;
        this.me = me;
        this.opponent = opponent;
        this.leader = leader;
        this.boatData = boatData == null ? new ArrayList<>() : boatData;
    }
}
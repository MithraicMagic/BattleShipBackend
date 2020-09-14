package com.bs.epic.battleships.events;

import com.bs.epic.battleships.documentation.annotations.Doc;
import com.bs.epic.battleships.game.Ship;

import java.util.ArrayList;
import java.util.Collection;

public class SetupData {
    @Doc("The unique id for the lobby")
    public int lobbyId;
    @Doc("The player's name")
    public String me;
    @Doc("The opponent's name")
    public String opponent;
    @Doc("Boolean indicating if the player is the lobby's leader")
    public boolean leader;

    @Doc("An array containing all the boats of the player")
    public Collection<Ship> boatData;

    public SetupData(int lobbyId, String me, String opponent, boolean leader, Collection<Ship> boatData) {
        this.lobbyId = lobbyId;
        this.me = me;
        this.opponent = opponent;
        this.leader = leader;
        this.boatData = boatData == null ? new ArrayList<>() : boatData;
    }
}
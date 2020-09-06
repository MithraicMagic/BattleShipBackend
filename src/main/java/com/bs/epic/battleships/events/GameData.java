package com.bs.epic.battleships.events;

import com.bs.epic.battleships.game.Ship;

import java.util.ArrayList;
import java.util.Collection;

public class GameData {
    public int lobbyId;
    public String me;
    public String opponent;
    public boolean leader;

    public Collection<Ship> boatData;

    public GameData(int lobbyId, String me, String opponent, boolean leader, Collection<Ship> boatData) {
        this.lobbyId = lobbyId;
        this.me = me;
        this.opponent = opponent;
        this.leader = leader;
        this.boatData = boatData == null ? new ArrayList<>() : boatData;
    }
}
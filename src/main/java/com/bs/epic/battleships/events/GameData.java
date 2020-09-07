package com.bs.epic.battleships.events;

import com.bs.epic.battleships.game.GridPos;
import com.bs.epic.battleships.game.Ship;

import java.util.Collection;

public class GameData {
    public int lobbyId;
    public String me;
    public String opponent;
    public boolean leader;

    public Collection<Ship> boatData;
    public HitMissData hitData;
    public HitMissData missData;

    public GameData(int lobbyId, String me, String opponent, boolean leader, Collection<Ship> boatData, HitMissData hitData, HitMissData missData) {
        this.lobbyId = lobbyId;
        this.me = me;
        this.opponent = opponent;
        this.leader = leader;
        this.boatData = boatData;
        this.hitData = hitData;
        this.missData = missData;
    }
}

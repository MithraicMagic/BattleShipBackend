package com.bs.epic.battleships.events;

import com.bs.epic.battleships.documentation.Doc;
import com.bs.epic.battleships.game.Ship;

import java.util.Collection;

public class GameData {
    @Doc("The unique id for the lobby")
    public int lobbyId;
    @Doc("The player's name")
    public String me;
    @Doc("The opponent's name")
    public String opponent;
    @Doc("Boolean stating if the player is the lobby's leader")
    public boolean leader;

    @Doc("Array containing all the player's boats")
    public Collection<Ship> boatData;
    @Doc("Array containing all shots that hit from both players")
    public HitMissData hitData;
    @Doc("Array containing all shots the missed from both players")
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

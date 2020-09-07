package com.bs.epic.battleships.events;

import com.bs.epic.battleships.game.GridPos;

import java.util.Collection;

public class HitMissData {
    public Collection<GridPos> player;
    public Collection<GridPos> opponent;

    public HitMissData(Collection<GridPos> player, Collection<GridPos> opponent) {
        this.player = player;
        this.opponent = opponent;
    }
}

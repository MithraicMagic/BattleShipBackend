package com.bs.epic.battleships.events;

import com.bs.epic.battleships.documentation.Doc;
import com.bs.epic.battleships.game.grid.GridPos;

import java.util.Collection;

public class HitMissData {
    @Doc(description = "Data from the player")
    public Collection<GridPos> player;
    @Doc(description = "Data from the opponent")
    public Collection<GridPos> opponent;

    public HitMissData(Collection<GridPos> player, Collection<GridPos> opponent) {
        this.player = player;
        this.opponent = opponent;
    }
}

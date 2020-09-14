package com.bs.epic.battleships.events;

import com.bs.epic.battleships.documentation.annotations.Doc;
import com.bs.epic.battleships.game.grid.GridPos;

public class ShootResult {
    @Doc("If a ship was hit this shot")
    public boolean hitShip;
    @Doc("If a ship was destroyed this shot")
    public boolean destroyedShip;

    @Doc("Position that was shot")
    public GridPos pos;

    public ShootResult(boolean hitShip, boolean destroyedShip, GridPos pos) {
        this.hitShip = hitShip;
        this.destroyedShip = destroyedShip;
        this.pos = pos;
    }
}

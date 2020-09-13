package com.bs.epic.battleships.events;

import com.bs.epic.battleships.documentation.Doc;
import com.bs.epic.battleships.game.GridPos;

public class ShootResult {
    @Doc(description = "If a ship was hit this shot")
    public boolean hitShip;
    @Doc(description = "If a ship was destroyed this shot")
    public boolean destroyedShip;

    @Doc(description = "Position that was shot")
    public GridPos pos;

    public ShootResult(boolean hitShip, boolean destroyedShip, GridPos pos) {
        this.hitShip = hitShip;
        this.destroyedShip = destroyedShip;
        this.pos = pos;
    }
}

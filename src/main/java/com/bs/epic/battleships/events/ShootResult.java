package com.bs.epic.battleships.util.result;

import com.bs.epic.battleships.game.GridPos;

public class ShootResult {
    public boolean hitShip;
    public boolean destroyedShip;

    public GridPos pos;

    public ShootResult(boolean hitShip, boolean destroyedShip, GridPos pos) {
        this.hitShip = hitShip;
        this.destroyedShip = destroyedShip;
        this.pos = pos;
    }
}

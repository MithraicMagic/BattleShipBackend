package com.bs.epic.battleships.util.result;

import com.bs.epic.battleships.events.ShootResult;
import com.bs.epic.battleships.game.grid.GridPos;

public class ShootSuccess extends Result {
    public ShootResult result;

    public ShootSuccess(boolean hitShip, boolean destroyedShip, GridPos pos, int boatsLeft) {
        super(true, null);
        this.result = new ShootResult(hitShip, destroyedShip, pos, boatsLeft);
    }
}

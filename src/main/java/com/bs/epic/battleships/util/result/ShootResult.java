package com.bs.epic.battleships.util.result;

public class ShootResult {
    public boolean hitShip;
    public boolean destroyedShip;

    public ShootResult(boolean hitShip, boolean destroyedShip) {
        this.hitShip = hitShip;
        this.destroyedShip = destroyedShip;
    }
}

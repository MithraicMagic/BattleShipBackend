package com.bs.epic.battleships.events;

import com.bs.epic.battleships.game.Ship;

import java.util.Collection;

public class Ships {
    public Collection<Ship> ships;

    public Ships(Collection<Ship> ships) {
        this.ships = ships;
    }
}

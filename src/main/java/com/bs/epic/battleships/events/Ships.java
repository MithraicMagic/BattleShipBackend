package com.bs.epic.battleships.events;

import com.bs.epic.battleships.documentation.Doc;
import com.bs.epic.battleships.game.Ship;

import java.util.Collection;

public class Ships {
    @Doc(description = "A collection of all the ships that were placed automatically")
    public Collection<Ship> ships;

    public Ships(Collection<Ship> ships) {
        this.ships = ships;
    }
}

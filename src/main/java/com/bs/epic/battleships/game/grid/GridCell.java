package com.bs.epic.battleships.game.grid;

import com.bs.epic.battleships.game.CellState;
import com.bs.epic.battleships.game.Ship;

public class GridCell {
    public CellState state;
    public Ship ship;

    public GridCell() {
        state = CellState.Water;
    }
}

package com.bs.epic.battleships.game;

public class GridCell {
    public CellState state;
    public Ship ship;

    public GridCell() {
        state = CellState.Water;
    }
}

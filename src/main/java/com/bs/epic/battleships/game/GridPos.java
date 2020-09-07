package com.bs.epic.battleships.game;

public class GridPos {
    public int i, j;

    public GridPos(int i, int j) {
        this.i = i;
        this.j = j;
    }

    public int index(int size) {
        return i + j * size;
    }
}

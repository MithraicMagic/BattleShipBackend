package com.bs.epic.battleships.game;

import com.bs.epic.battleships.util.Util;

public class GridPos {
    public int i, j;

    public GridPos(int i, int j) {
        this.i = i;
        this.j = j;
    }

    static public GridPos random() {
        return new GridPos(Util.randomInt(0, 9), Util.randomInt(0, 9));
    }

    public int index(int size) {
        return i + j * size;
    }
}

package com.bs.epic.battleships.game.grid;

import java.util.ArrayList;

public class Grid {
    private ArrayList<GridCell> cells;
    private int size;

    public Grid() {
        cells = new ArrayList<>();
    }

    public void init(int size) {
        this.size = size;

        for (int j = 0; j < size; j++) {
            for (int i = 0; i < size; i++) {
                cells.add(new GridCell());
            }
        }
    }

    public GridCell get(GridPos p) {
        return cells.get(p.index(size));
    }

    public void clear() {
        cells.clear();
    }

    public ArrayList<GridCell> cells() {
        return cells;
    }
}

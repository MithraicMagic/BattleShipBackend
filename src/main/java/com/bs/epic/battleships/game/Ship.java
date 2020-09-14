package com.bs.epic.battleships.game;

import com.bs.epic.battleships.game.grid.GridPos;

public class Ship {
    public String name;
    public int length;
    public int hitPieces;

    public GridPos pos;
    public boolean horizontal;

    public Ship(String name, int length) {
        this.name = name;
        this.length = length;
        this.hitPieces = 0;
        this.horizontal = false;
    }

    public Ship(Ship s, GridPos pos, boolean horizontal) {
        this.name = s.name;
        this.length = s.length;
        this.hitPieces = 0;
        this.horizontal = false;
        this.pos = pos;
        this.horizontal = horizontal;
    }

    public boolean isDestroyed() {
        return length == hitPieces;
    }
}

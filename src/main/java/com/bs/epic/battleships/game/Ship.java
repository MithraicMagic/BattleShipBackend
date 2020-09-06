package com.bs.epic.battleships.game;

public class Ship {
    public String name;
    public int length;
    public int hitPieces;

    public int i, j;
    public boolean horizontal;

    public Ship(String name, int length) {
        this.name = name;
        this.length = length;
        this.hitPieces = 0;
        this.horizontal = false;
    }

    public Ship(Ship s, int i, int j, boolean horizontal) {
        this.name = s.name;
        this.length = s.length;
        this.hitPieces = 0;
        this.horizontal = false;
        this.i = i;
        this.j = j;
        this.horizontal = horizontal;
    }

    public boolean isDestroyed() {
        return length == hitPieces;
    }
}

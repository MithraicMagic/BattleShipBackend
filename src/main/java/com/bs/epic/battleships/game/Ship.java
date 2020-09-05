package com.bs.epic.battleships.game;

public class Ship {
    public String name;
    public int length;
    public int hitPieces;

    public Ship(String name, int length) {
        this.name = name;
        this.length = length;
        this.hitPieces = 0;
    }

    public boolean isDestroyed() {
        return length == hitPieces;
    }
}

package com.bs.epic.battleships.game.grid;

public enum GridDirection {
    NONE(0, 0), LEFT(-1, 0), RIGHT(1, 0), UP(0, -1), DOWN(0, 1);

    public final int dirX, dirY;
    GridDirection(int dirX, int dirY) {
        this.dirX = dirX;
        this.dirY = dirY;
    }

    public GridDirection next() {
        switch (this) {
            case LEFT: return GridDirection.RIGHT;
            case RIGHT: return GridDirection.UP;
            case UP: return GridDirection.DOWN;
            case DOWN:
            case NONE:
            default:
                return GridDirection.LEFT;
        }
    }
}

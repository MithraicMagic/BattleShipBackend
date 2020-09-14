package com.bs.epic.battleships.events;

import com.bs.epic.battleships.documentation.Doc;

public class PlaceShip {
    @Doc("The unique id for the lobby")
    public int lobbyId;
    @Doc("The coordinates at which the player wants to place his/her ship")
    public int i, j;

    @Doc("The unique id for the player")
    public String uid;
    @Doc("The name of the ship that the player wants to place")
    public String ship;

    @Doc("A boolean indicating if the ship is horizontal (or vertical)")
    public boolean horizontal;

    public PlaceShip() {}

    public PlaceShip(int lobbyId, int i, int j, String uid, String ship, boolean horizontal) {
        this.lobbyId = lobbyId;
        this.i = i;
        this.j = j;

        this.uid = uid;
        this.ship = ship;

        this.horizontal = horizontal;
    }

    @Override
    public String toString() {
        return "PlaceShip{" +
                "lobbyId=" + lobbyId +
                ", i=" + i +
                ", j=" + j +
                ", uid='" + uid + '\'' +
                ", ship='" + ship + '\'' +
                ", horizontal=" + horizontal +
                '}';
    }
}

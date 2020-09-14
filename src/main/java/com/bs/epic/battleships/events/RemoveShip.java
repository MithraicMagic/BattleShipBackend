package com.bs.epic.battleships.events;

import com.bs.epic.battleships.documentation.annotations.Doc;

public class RemoveShip {
    @Doc("The unique id for the lobby")
    public int lobbyId;
    @Doc("The name of the ship that the player wants removed")
    public String ship;
    @Doc("The unique id for the player")
    public String uid;

    public RemoveShip() {}

    public RemoveShip(int lobbyId, String ship, String uid) {
        this.lobbyId = lobbyId;
        this.ship = ship;
        this.uid = uid;
    }
}

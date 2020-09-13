package com.bs.epic.battleships.events;

import com.bs.epic.battleships.documentation.Doc;

public class RemoveShip {
    @Doc(description = "The unique id for the lobby")
    public int lobbyId;
    @Doc(description = "The name of the ship that the player wants removed")
    public String ship;
    @Doc(description = "The unique id for the player")
    public String uid;

    public RemoveShip() {}

    public RemoveShip(int lobbyId, String ship, String uid) {
        this.lobbyId = lobbyId;
        this.ship = ship;
        this.uid = uid;
    }
}

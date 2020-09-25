package com.bs.epic.battleships.events;

import com.bs.epic.battleships.documentation.annotations.Doc;

public class LoggedInUserWon {
    @Doc("The user's JSON Web Token")
    public String jwt;
    @Doc("The lobby's id")
    public int lobbyId;
    @Doc("The user's id")
    public String uid;

    public LoggedInUserWon() {}

    public LoggedInUserWon(String jwt, int lobbyId, String uid) {
        this.jwt = jwt;
        this.lobbyId = lobbyId;
        this.uid = uid;
    }
}

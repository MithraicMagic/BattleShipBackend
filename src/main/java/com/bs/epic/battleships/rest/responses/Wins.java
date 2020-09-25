package com.bs.epic.battleships.rest.responses;

import com.bs.epic.battleships.documentation.annotations.Doc;

public class Wins {
    @Doc("The user's single player game wins")
    public int sp;
    @Doc("The user's multiplayer game wins")
    public int mp;

    public Wins(int sp, int mp) {
        this.sp = sp;
        this.mp = mp;
    }
}

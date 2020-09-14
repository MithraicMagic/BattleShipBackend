package com.bs.epic.battleships.events;

import com.bs.epic.battleships.documentation.annotations.Doc;
import com.bs.epic.battleships.user.UserState;

public class State {
    @Doc("The player's current state")
    public String state;

    public State() {}

    public State(UserState state) {
        this.state = state.toString();
    }
}

package com.bs.epic.battleships.events;

import com.bs.epic.battleships.user.UserState;

public class State {
    public String state;

    public State(UserState state) {
        this.state = state.toString();
    }
}

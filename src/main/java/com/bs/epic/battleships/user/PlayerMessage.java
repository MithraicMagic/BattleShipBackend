package com.bs.epic.battleships.user;

import java.time.LocalTime;

public class PlayerMessage {
    public String message;
    public LocalTime time;

    public PlayerMessage(String message) {
        this.message = message;
        this.time = LocalTime.now();
    }
}

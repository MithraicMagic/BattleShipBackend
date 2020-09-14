package com.bs.epic.battleships.user.player;

import java.time.LocalTime;

public class PlayerMessage {
    public String message;
    public LocalTime time;
    public String sender;
    public String receiver;

    public PlayerMessage(String message, String sender, String receiver) {
        this.message = message;
        this.sender = sender;
        this.receiver = receiver;
        this.time = LocalTime.now();
    }
}

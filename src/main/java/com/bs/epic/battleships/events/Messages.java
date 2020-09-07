package com.bs.epic.battleships.events;

import com.bs.epic.battleships.user.PlayerMessage;

import java.util.Collection;

public class Messages {
    public Collection<PlayerMessage> sent;
    public Collection<PlayerMessage> received;

    public Messages(Collection<PlayerMessage> sent, Collection<PlayerMessage> received) {
        this.sent = sent;
        this.received = received;
    }
}

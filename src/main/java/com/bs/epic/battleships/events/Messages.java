package com.bs.epic.battleships.events;

import java.util.Collection;

public class Messages {
    public Collection<String> sent;
    public Collection<String> received;

    public Messages(Collection<String> sent, Collection<String> received) {
        this.sent = sent;
        this.received = received;
    }
}

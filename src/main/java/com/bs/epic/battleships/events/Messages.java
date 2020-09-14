package com.bs.epic.battleships.events;

import com.bs.epic.battleships.documentation.Doc;
import com.bs.epic.battleships.user.player.PlayerMessage;

import java.util.Collection;

public class Messages {
    @Doc(description = "All the messages that the player sent and received")
    public Collection<PlayerMessage> messages;

    public Messages(Collection<PlayerMessage> messages) {
        this.messages = messages;
    }
}

package com.bs.epic.battleships.rest.responses;

import com.bs.epic.battleships.documentation.annotations.Doc;
import com.bs.epic.battleships.rest.repository.dto.AiMessage;

import java.util.List;

public class Messages {
    @Doc("All the messages")
    public List<AiMessage> messages;

    public Messages() {}

    public Messages(List<AiMessage> messages) {
        this.messages = messages;
    }
}

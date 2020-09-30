package com.bs.epic.battleships.user.ai.behaviour;


import com.bs.epic.battleships.rest.repository.dto.AiMessage;
import com.bs.epic.battleships.rest.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MessageHandler {
    @Autowired
    private MessageService messageService;
    private static MessageHandler instance = new MessageHandler();

    public static MessageHandler getInstance() {
        return instance;
    }

    public List<AiMessage> getAiMessage() {
        return messageService.getAiMessages();
    }
}

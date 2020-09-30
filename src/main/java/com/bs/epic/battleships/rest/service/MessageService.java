package com.bs.epic.battleships.rest.service;

import com.bs.epic.battleships.rest.repository.AiMessageRepository;
import com.bs.epic.battleships.rest.repository.dto.AiMessage;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {
    private final AiMessageRepository aiMessageRepository;

    public MessageService(AiMessageRepository aiMessageRepository) {
        this.aiMessageRepository = aiMessageRepository;
    }

    public List<AiMessage> getAiMessages() {
        return aiMessageRepository.findAll();
    }

    public AiMessage add(String text) {
        return aiMessageRepository.save(new AiMessage(text));
    }

    public void remove(long id) {
        aiMessageRepository.deleteById(id);
    }
}

package com.bs.epic.battleships.unit.rest.service;

import com.bs.epic.battleships.rest.repository.AiMessageRepository;
import com.bs.epic.battleships.rest.repository.dto.AiMessage;
import com.bs.epic.battleships.rest.service.MessageService;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.*;

public class MessageServiceTest {
    AiMessageRepository aiMessageRepository = mock(AiMessageRepository.class);
    MessageService messageService = new MessageService(aiMessageRepository);

    @Test
    public void testGetAiMessages() {
        List<AiMessage> messages = List.of(new AiMessage("Hallo"), new AiMessage("Yay, ik heb gewonnen!"));
        when(aiMessageRepository.findAll()).thenReturn(messages);
        Assert.assertEquals(messages, messageService.getAiMessages());
    }

    @Test
    public void testAdd() {
        messageService.add("Een nieuw bericht!");
        verify(aiMessageRepository, times(1)).save(any());
    }

    @Test
    public void testRemove() {
        messageService.remove(6);
        verify(aiMessageRepository, times(1)).deleteById(any());
    }
}

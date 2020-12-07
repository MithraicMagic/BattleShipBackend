package com.bs.epic.battleships.unit.rest.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import com.bs.epic.battleships.rest.controller.MessageController;
import com.bs.epic.battleships.rest.repository.dto.AiMessage;
import com.bs.epic.battleships.rest.requestbodies.AddMessage;
import com.bs.epic.battleships.rest.service.MessageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(SpringExtension.class)
public class MessageControllerTest {
    private MockMvc mockMvc;

    @Mock
    private MessageService messageService;
    @InjectMocks
    private MessageController messageController;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(messageController).build();
    }

    private String jsonString(Object obj) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(obj);
    }

    @Test
    public void getAllTest() throws Exception {
        when(messageService.getAiMessages()).thenReturn(new ArrayList<>());
        mockMvc.perform(get("/message/all")).andExpect(status().is2xxSuccessful());
    }

    @Test
    public void addTest() throws Exception {
        when(messageService.getAiMessages()).thenReturn(new ArrayList<>());

        mockMvc.perform(post("/message/add")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonString(new AddMessage()))
        ).andExpect(status().is2xxSuccessful());
    }

    @Test
    public void addAlreadyExistingTest() throws Exception {
        when(messageService.getAiMessages()).thenReturn(List.of(new AiMessage("hoi")));

        mockMvc.perform(post("/message/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonString(new AddMessage("hoi")))
        ).andExpect(status().is(422));
    }

    @Test
    public void removeTest() throws Exception {
        when(messageService.getAiMessages()).thenReturn(List.of(new AiMessage("hoi")));
        mockMvc.perform(post("/message/remove/{id}", 5)).andExpect(status().is2xxSuccessful());
    }
}

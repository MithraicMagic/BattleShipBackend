package com.bs.epic.battleships.unit.rest.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bs.epic.battleships.rest.controller.DocumentationController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(SpringExtension.class)
public class DocumentationControllerTest {
    private MockMvc mockMvc;

    @Mock
    private DocumentationController documentationController;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(documentationController).build();
    }

    @Test
    public void getSocketsTest() throws Exception {
        mockMvc.perform(get("/documentation/sockets")).andExpect(status().is2xxSuccessful());
    }

    @Test
    public void getRestTest() throws Exception {
        mockMvc.perform(get("/documentation/rest")).andExpect(status().is2xxSuccessful());
    }
}

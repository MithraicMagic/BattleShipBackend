package com.bs.epic.battleships.unit.rest.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import com.bs.epic.battleships.rest.controller.UserController;
import com.bs.epic.battleships.rest.repository.dto.User;
import com.bs.epic.battleships.rest.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(SpringExtension.class)
public class UserControllerTest {
    private MockMvc mockMvc;

    @Mock
    private AuthService authService;
    @InjectMocks
    private UserController userController;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    public void getMeTest() throws Exception {
        when(authService.getUser()).thenReturn(Optional.of(new User()));
        mockMvc.perform(get("/user/me")).andExpect(status().is2xxSuccessful());
    }

    @Test
    public void getMeFailsTest() throws Exception {
        when(authService.getUser()).thenReturn(Optional.empty());
        mockMvc.perform(get("/user/me")).andExpect(status().is4xxClientError());
    }

    @Test
    public void getWinsTest() throws Exception {
        when(authService.getUser()).thenReturn(Optional.of(new User()));
        mockMvc.perform(get("/user/wins")).andExpect(status().is2xxSuccessful());
    }

    @Test
    public void getWinsNotFoundTest() throws Exception {
        when(authService.getUser()).thenReturn(Optional.empty());
        mockMvc.perform(get("/user/wins")).andExpect(status().is4xxClientError());
    }
}

package com.bs.epic.battleships.unit.rest.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bs.epic.battleships.rest.controller.AuthController;
import com.bs.epic.battleships.rest.requestbodies.Login;
import com.bs.epic.battleships.rest.requestbodies.Register;
import com.bs.epic.battleships.rest.service.AuthService;
import com.bs.epic.battleships.verification.JwtUtil;
import com.bs.epic.battleships.verification.Result;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
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
public class AuthControllerTest {
    private MockMvc mockMvc;

    @Mock
    private AuthService authService;
    @Mock
    private JwtUtil jwtUtil;
    @InjectMocks
    private AuthController authController;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    private String jsonString(Object obj) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(obj);
    }

    @Test
    public void loginTest() throws Exception {
        when(authService.login(any())).thenReturn(Result.success());

        mockMvc.perform(post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonString(new Login("rens@email.com", "password")))
        ).andExpect(status().is2xxSuccessful());

        verify(jwtUtil, times(1)).generateToken(any());
    }

    @Test
    public void loginTestWithNoContent() throws Exception {
        when(authService.login(any())).thenReturn(Result.success());

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().is4xxClientError());

        verifyNoInteractions(jwtUtil);
    }

    @Test
    public void loginTestWithIncorrectLogin() throws Exception {
        when(authService.login(any())).thenReturn(Result.error(422, "error"));

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonString(new Login("rens@", "password")))
        ).andExpect(status().is(422));

        verifyNoInteractions(jwtUtil);
    }

    @Test
    public void registerTest() throws Exception {
        when(authService.register(any())).thenReturn(Result.success());

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonString(new Register("rens", "rens@email.com", "password")))
        ).andExpect(status().is2xxSuccessful());
    }

    @Test
    public void registerTestFail() throws Exception {
        when(authService.register(any())).thenReturn(Result.error(422, "Error"));

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonString(new Register("rens", "rens@email.com", "password")))
        ).andExpect(status().is(422));
    }
}

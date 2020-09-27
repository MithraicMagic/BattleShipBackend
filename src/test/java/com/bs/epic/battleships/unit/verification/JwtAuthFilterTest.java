package com.bs.epic.battleships.unit.verification;


import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import com.bs.epic.battleships.rest.repository.dto.User;
import com.bs.epic.battleships.rest.service.AuthService;
import com.bs.epic.battleships.verification.JwtAuthFilter;
import com.bs.epic.battleships.verification.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class JwtAuthFilterTest {
    private JwtUtil jwtUtil = new JwtUtil("1234567891011121314151617181920AbCDeFghiJKLMNOpqrstU@");
    private User user;

    private String validJwt = "";

    private AuthService authService;
    private JwtAuthFilter jwtAuthFilter;

    @BeforeEach
    public void Setup() {
        user = new User("rens", "rens@gmail.com", "password");
        validJwt = jwtUtil.generateToken(user);

        authService = Mockito.mock(AuthService.class);
        jwtAuthFilter = new JwtAuthFilter("Authorization", jwtUtil, authService);

        when(authService.getByUsername(any())).thenReturn(Optional.of(user));
    }

    @Test
    public void testVerifyJwt() {
        var token = jwtAuthFilter.verifyJwt("Bearer " + validJwt, Mockito.mock(HttpServletRequest.class));
        assertNotEquals(null, token);
    }
}

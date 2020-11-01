package com.bs.epic.battleships.unit.sockets;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.bs.epic.battleships.SocketEvents;
import com.bs.epic.battleships.lobby.Lobby;
import com.bs.epic.battleships.lobby.LobbyManager;
import com.bs.epic.battleships.rest.service.AuthService;
import com.bs.epic.battleships.rest.service.MessageService;
import com.bs.epic.battleships.user.UserManager;
import com.bs.epic.battleships.verification.AuthValidator;
import com.bs.epic.battleships.verification.JwtUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

public class OnLastUidTest {
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private UserManager userManager;
    @Mock
    private LobbyManager lobbyManager;
    @Mock
    private AuthValidator authValidator;
    @Mock
    private AuthService authService;
    @Mock
    private MessageService messageService;

    @InjectMocks
    private SocketEvents socketEvents;

    @BeforeAll
    public static void beforeAll() {
    }

    @BeforeEach
    public void beforeEach() {
    }
}

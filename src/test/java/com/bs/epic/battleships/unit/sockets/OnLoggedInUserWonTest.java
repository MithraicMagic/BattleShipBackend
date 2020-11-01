package com.bs.epic.battleships.unit.sockets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.util.Optional;

import com.bs.epic.battleships.SocketEvents;
import com.bs.epic.battleships.events.ErrorEvent;
import com.bs.epic.battleships.events.LoggedInUserWon;
import com.bs.epic.battleships.lobby.Lobby;
import com.bs.epic.battleships.lobby.LobbyManager;
import com.bs.epic.battleships.rest.repository.dto.User;
import com.bs.epic.battleships.rest.service.AuthService;
import com.bs.epic.battleships.rest.service.MessageService;
import com.bs.epic.battleships.user.UserManager;
import com.bs.epic.battleships.user.player.Player;
import com.bs.epic.battleships.verification.JwtUtil;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

public class OnLoggedInUserWonTest {
    private SocketEvents socketEvents;

    private JwtUtil jwtUtil = mock(JwtUtil.class);

    private UserManager userManager = mock(UserManager.class);
    private LobbyManager lobbyManager = mock(LobbyManager.class);

    private SocketIOClient socketOne = mock(SocketIOClient.class);
    private SocketIOClient socketTwo = mock(SocketIOClient.class);

    private MessageService messageService = mock(MessageService.class);
    private AuthService authService = mock(AuthService.class);

    private AckRequest ackRequest = mock(AckRequest.class);

    private Player playerOne = new Player("Player1", socketOne, "Code1");
    private Player playerTwo = new Player("Player2", socketTwo, "Code2");

    private Lobby lobby = new Lobby(5, playerOne, playerTwo);

    @BeforeEach
    public void beforeEach() {
        socketEvents = new SocketEvents(jwtUtil, userManager, lobbyManager, null, authService, messageService);
        lobby.initGame(10);
    }

    @Test
    public void testInvalidToken() {
        var errorCaptor = ArgumentCaptor.forClass(ErrorEvent.class);
        var data = new LoggedInUserWon("jwt", 5, playerOne.uid);

        when(jwtUtil.extractUsername("jwt")).thenReturn(null);

        socketEvents.onLoggedInUserWon(socketOne, data, ackRequest);
        verify(socketOne).sendEvent(eq("errorEvent"), errorCaptor.capture());

        var error = errorCaptor.getValue();
        assertEquals("Invalid token.", error.reason);
    }

    @Test
    public void testLobbyIsNull() {
        var errorCaptor = ArgumentCaptor.forClass(ErrorEvent.class);
        var data = new LoggedInUserWon("jwt", 5, playerOne.uid);

        when(jwtUtil.extractUsername("jwt")).thenReturn("rens");

        socketEvents.onLoggedInUserWon(socketOne, data, ackRequest);
        verify(socketOne).sendEvent(eq("errorEvent"), errorCaptor.capture());

        var error = errorCaptor.getValue();
        assertEquals("Invalid lobby.", error.reason);
    }

    @Test
    public void testInvalidUser() {
        var errorCaptor = ArgumentCaptor.forClass(ErrorEvent.class);
        var data = new LoggedInUserWon("jwt", 5, playerOne.uid);

        when(jwtUtil.extractUsername("jwt")).thenReturn("rens");
        when(lobbyManager.getLobby(5)).thenReturn(lobby);
        when(authService.getByUsername("rens")).thenReturn(Optional.empty());

        socketEvents.onLoggedInUserWon(socketOne, data, ackRequest);
        verify(socketOne).sendEvent(eq("errorEvent"), errorCaptor.capture());

        var error = errorCaptor.getValue();
        assertEquals("Invalid player", error.reason);
    }

    @Test
    public void test() {
        var data = new LoggedInUserWon("jwt", 5, playerOne.uid);
        var user = new User();
        user.spWins = 0;
        user.mpWins = 0;

        when(jwtUtil.extractUsername("jwt")).thenReturn("rens");
        when(lobbyManager.getLobby(5)).thenReturn(lobby);
        when(authService.getByUsername("rens")).thenReturn(Optional.of(user));

        socketEvents.onLoggedInUserWon(socketOne, data, ackRequest);
        verify(authService, times(1)).saveUser(user);
    }
}

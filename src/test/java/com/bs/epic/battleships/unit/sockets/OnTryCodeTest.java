package com.bs.epic.battleships.unit.sockets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import static org.mockito.Mockito.*;

import com.bs.epic.battleships.SocketEvents;
import com.bs.epic.battleships.events.Code;
import com.bs.epic.battleships.events.ErrorEvent;
import com.bs.epic.battleships.lobby.Lobby;
import com.bs.epic.battleships.lobby.LobbyManager;
import com.bs.epic.battleships.user.UserManager;
import com.bs.epic.battleships.user.player.Player;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

public class OnTryCodeTest {
    private SocketEvents socketEvents;

    private UserManager userManager = mock(UserManager.class);
    private LobbyManager lobbyManager = mock(LobbyManager.class);

    private SocketIOClient socketOne = mock(SocketIOClient.class);
    private SocketIOClient socketTwo = mock(SocketIOClient.class);

    private AckRequest ackRequest = mock(AckRequest.class);

    private Code code = new Code("AAA");
    private Player playerOne = new Player("Name", socketOne, "Code");
    private Player playerTwo = new Player("Player2", socketTwo, "Code2");

    @BeforeEach
    public void beforeEach() {
        socketEvents = new SocketEvents(null, userManager, lobbyManager, null, null, null);
    }

    @Test
    public void testInvalidUser() {
        var errorCaptor = ArgumentCaptor.forClass(ErrorEvent.class);

        when(userManager.getBySocket(socketOne)).thenReturn(null);

        socketEvents.onTryCode(socketOne, code, ackRequest);

        verify(socketOne).sendEvent(eq("errorEvent"), errorCaptor.capture());

        var error = errorCaptor.getValue();
        assertEquals("Something went horribly wrong. Try refreshing the page.", error.reason);
    }

    @Test
    public void testCurrentEqualsOther() {
        var errorCaptor = ArgumentCaptor.forClass(ErrorEvent.class);

        when(userManager.getBySocket(socketOne)).thenReturn(playerOne);
        when(userManager.getByCode(code.code)).thenReturn(playerOne);

        socketEvents.onTryCode(socketOne, code, ackRequest);

        verify(socketOne).sendEvent(eq("errorEvent"), errorCaptor.capture());

        var error = errorCaptor.getValue();
        assertEquals("You can't enter your own lobby.", error.reason);
    }

    @Test
    public void testOtherNull() {
        var errorCaptor = ArgumentCaptor.forClass(ErrorEvent.class);

        when(userManager.getBySocket(socketOne)).thenReturn(playerOne);
        when(userManager.getByCode(code.code)).thenReturn(null);

        socketEvents.onTryCode(socketOne, code, ackRequest);

        verify(socketOne).sendEvent(eq("errorEvent"), errorCaptor.capture());

        var error = errorCaptor.getValue();
        assertEquals("You did not enter a valid code!", error.reason);
    }

    @Test
    public void testJoin() {
        when(userManager.getBySocket(socketOne)).thenReturn(playerOne);
        when(userManager.getByCode(code.code)).thenReturn(playerTwo);

        socketEvents.onTryCode(socketOne, code, ackRequest);

        verify(socketOne, times(1)).sendEvent(eq("lobbyJoined"), any());
        verify(socketTwo, times(1)).sendEvent(eq("lobbyJoined"), any());
    }
}
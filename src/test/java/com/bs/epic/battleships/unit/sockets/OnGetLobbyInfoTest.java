package com.bs.epic.battleships.unit.sockets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.junit.Assert.*;

import com.bs.epic.battleships.SocketEvents;
import com.bs.epic.battleships.events.Code;
import com.bs.epic.battleships.events.ErrorEvent;
import com.bs.epic.battleships.lobby.LobbyManager;
import com.bs.epic.battleships.user.UserManager;
import com.bs.epic.battleships.user.player.Player;
import com.bs.epic.battleships.verification.AuthValidator;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

public class OnGetLobbyInfoTest {
    private SocketEvents socketEvents;

    private UserManager userManager = mock(UserManager.class);
    private LobbyManager lobbyManager = mock(LobbyManager.class);

    private SocketIOClient socket = mock(SocketIOClient.class);

    private AckRequest ackRequest = mock(AckRequest.class);

    private Player user = new Player("Name", socket, "Code");

    @BeforeEach
    public void beforeEach() {
        socketEvents = new SocketEvents(null, userManager, lobbyManager, new AuthValidator(), null, null);
    }

    @Test
    public void testInvalidLobbyCode() {
        var errorCaptor = ArgumentCaptor.forClass(ErrorEvent.class);

        when(userManager.getByCode("Code")).thenReturn(null);

        socketEvents.onGetLobbyInfo(socket, new Code("Code"), ackRequest);
        verify(socket).sendEvent(eq("errorEvent"), errorCaptor.capture());

        var error = errorCaptor.getValue();
        assertEquals("User does not exist", error.reason);
    }

    @Test
    public void testValidLobbyCode() {
        when(userManager.getByCode("Code")).thenReturn(user);

        socketEvents.onGetLobbyInfo(socket, new Code("Code"), ackRequest);
        verify(socket, times(1)).sendEvent(eq("lobbyInfo"), any());
    }
}

package com.bs.epic.battleships.unit.sockets;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.bs.epic.battleships.SocketEvents;
import com.bs.epic.battleships.events.ErrorEvent;
import com.bs.epic.battleships.events.LeaveLobby;
import com.bs.epic.battleships.lobby.Lobby;
import com.bs.epic.battleships.lobby.LobbyManager;
import com.bs.epic.battleships.rest.service.MessageService;
import com.bs.epic.battleships.user.UserManager;
import com.bs.epic.battleships.user.player.Player;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

public class OnLeaveLobby {
    private SocketEvents socketEvents;

    private UserManager userManager = mock(UserManager.class);
    private LobbyManager lobbyManager = mock(LobbyManager.class);

    private SocketIOClient socketOne = mock(SocketIOClient.class);
    private SocketIOClient socketTwo = mock(SocketIOClient.class);

    private MessageService messageService = mock(MessageService.class);

    private AckRequest ackRequest = mock(AckRequest.class);

    private Player playerOne = new Player("Name", socketOne, "Code");
    private Player playerTwo = new Player("Name2", socketTwo, "Code2");

    private LeaveLobby data = new LeaveLobby(playerOne.uid, 5);

    private Lobby lobby = new Lobby(5, playerOne, playerTwo);

    @BeforeEach
    public void beforeEach() {
        socketEvents = new SocketEvents(null, userManager, lobbyManager, null, null, messageService);
    }

    @Test
    public void testPlayerNull() {
        var errorCaptor = ArgumentCaptor.forClass(ErrorEvent.class);

        socketEvents.onLeaveLobby(socketOne, data, ackRequest);
        verify(socketOne).sendEvent(eq("errorEvent"), errorCaptor.capture());

        var error = errorCaptor.getValue();
        assertEquals("Something went horribly wrong. Try refreshing the page.", error.reason);
    }

    @Test
    public void testLobbyNull() {
        var errorCaptor = ArgumentCaptor.forClass(ErrorEvent.class);

        when(userManager.getUser(playerOne.uid)).thenReturn(playerOne);
        when(lobbyManager.getLobby(5)).thenReturn(null);

        socketEvents.onLeaveLobby(socketOne, data, ackRequest);
        verify(socketOne).sendEvent(eq("errorEvent"), errorCaptor.capture());

        var error = errorCaptor.getValue();
        assertEquals("You tried to leave a lobby that doesn't exist.", error.reason);
    }
    
    @Test
    public void testLeaveNonPlayerLobby() {
        var errorCaptor = ArgumentCaptor.forClass(ErrorEvent.class);

        when(userManager.getUser(playerOne.uid)).thenReturn(playerOne);
        when(lobbyManager.getLobby(5)).thenReturn(mock(Lobby.class));

        socketEvents.onLeaveLobby(socketOne, data, ackRequest);
        verify(socketOne).sendEvent(eq("errorEvent"), errorCaptor.capture());

        var error = errorCaptor.getValue();
        assertEquals("You tried leaving a lobby that you're not a part of.", error.reason);
    }

    @Test
    public void test() {
        when(userManager.getUser(playerOne.uid)).thenReturn(playerOne);
        when(lobbyManager.getLobby(5)).thenReturn(lobby);

        socketEvents.onLeaveLobby(socketOne, data, ackRequest);
        verify(socketOne, never()).sendEvent(eq("errorEvent"), any());
    }
}

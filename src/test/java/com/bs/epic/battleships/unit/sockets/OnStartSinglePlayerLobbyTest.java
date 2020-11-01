package com.bs.epic.battleships.unit.sockets;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import com.bs.epic.battleships.SocketEvents;
import com.bs.epic.battleships.events.ErrorEvent;
import com.bs.epic.battleships.events.StartSinglePlayerLobby;
import com.bs.epic.battleships.lobby.LobbyManager;
import com.bs.epic.battleships.rest.service.MessageService;
import com.bs.epic.battleships.user.UserManager;
import com.bs.epic.battleships.user.player.Player;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

public class OnStartSinglePlayerLobbyTest {
    private SocketEvents socketEvents;

    private UserManager userManager = mock(UserManager.class);
    private LobbyManager lobbyManager = mock(LobbyManager.class);

    private SocketIOClient socket = mock(SocketIOClient.class);

    private MessageService messageService = mock(MessageService.class);

    private AckRequest ackRequest = mock(AckRequest.class);

    private Player player = new Player("Name", socket, "Code");

    @BeforeEach
    public void beforeEach() {
        socketEvents = new SocketEvents(null, userManager, lobbyManager, null, null, messageService);
    }

    @Test
    public void testPlayerNull() {
        var data = new StartSinglePlayerLobby("UID", 1, 1000);
        var errorCaptor = ArgumentCaptor.forClass(ErrorEvent.class);

        socketEvents.onStartSinglePlayerLobby(socket, data, ackRequest);
        verify(socket).sendEvent(eq("errorEvent"), errorCaptor.capture());

        var error = errorCaptor.getValue();
        assertEquals("Invalid player.", error.reason);
    }

    @Test
    public void testPlayerDelayTooLow() {
        var data = new StartSinglePlayerLobby("UID", 1, 2);
        var errorCaptor = ArgumentCaptor.forClass(ErrorEvent.class);

        when(userManager.getPlayer("UID")).thenReturn(player);

        socketEvents.onStartSinglePlayerLobby(socket, data, ackRequest);
        verify(socket).sendEvent(eq("errorEvent"), errorCaptor.capture());

        var error = errorCaptor.getValue();
        assertEquals("Delay should be between 100ms and 10000ms", error.reason);
    }

    @Test
    public void testPlayerDelayTooHigh() {
        var data = new StartSinglePlayerLobby("UID", 1, 20000);
        var errorCaptor = ArgumentCaptor.forClass(ErrorEvent.class);

        when(userManager.getPlayer("UID")).thenReturn(player);

        socketEvents.onStartSinglePlayerLobby(socket, data, ackRequest);
        verify(socket).sendEvent(eq("errorEvent"), errorCaptor.capture());

        var error = errorCaptor.getValue();
        assertEquals("Delay should be between 100ms and 10000ms", error.reason);
    }

    @Test
    public void testPlayerDifficultyTooLow() {
        var data = new StartSinglePlayerLobby("UID", -1, 1000);
        var errorCaptor = ArgumentCaptor.forClass(ErrorEvent.class);

        when(userManager.getPlayer("UID")).thenReturn(player);

        socketEvents.onStartSinglePlayerLobby(socket, data, ackRequest);
        verify(socket).sendEvent(eq("errorEvent"), errorCaptor.capture());

        var error = errorCaptor.getValue();
        assertEquals("Invalid difficulty", error.reason);
    }

    @Test
    public void testPlayerDifficultyTooHigh() {
        var data = new StartSinglePlayerLobby("UID", 50, 1000);
        var errorCaptor = ArgumentCaptor.forClass(ErrorEvent.class);

        when(userManager.getPlayer("UID")).thenReturn(player);

        socketEvents.onStartSinglePlayerLobby(socket, data, ackRequest);
        verify(socket).sendEvent(eq("errorEvent"), errorCaptor.capture());

        var error = errorCaptor.getValue();
        assertEquals("Invalid difficulty", error.reason);
    }

    @Test
    public void test() {
        var data = new StartSinglePlayerLobby("UID", 1, 1000);
        when(userManager.getPlayer("UID")).thenReturn(player);

        socketEvents.onStartSinglePlayerLobby(socket, data, ackRequest);
        verify(socket, never()).sendEvent(eq("errorEvent"), any());
    }
}

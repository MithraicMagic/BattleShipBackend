package com.bs.epic.battleships.unit.sockets;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import com.bs.epic.battleships.SocketEvents;
import com.bs.epic.battleships.events.ErrorEvent;
import com.bs.epic.battleships.events.LobbyId;
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
import org.mockito.Mockito;

public class OnStartup {
    private SocketEvents socketEvents;

    private UserManager userManager = mock(UserManager.class);
    private LobbyManager lobbyManager = mock(LobbyManager.class);

    private SocketIOClient socket = mock(SocketIOClient.class);

    private MessageService messageService = mock(MessageService.class);

    private AckRequest ackRequest = mock(AckRequest.class);

    private LobbyId data = new LobbyId(5);
    private Lobby lobby = mock(Lobby.class);

    @BeforeEach
    public void beforeEach() {
        socketEvents = new SocketEvents(null, userManager, lobbyManager, null, null, messageService);
    }

    @Test
    public void testPlayerIsNull() {
        var errorCaptor = ArgumentCaptor.forClass(ErrorEvent.class);

        socketEvents.onStartup(socket, data, ackRequest);
        verify(socket).sendEvent(eq("errorEvent"), errorCaptor.capture());

        var error = errorCaptor.getValue();
        assertEquals("Invalid lobby.", error.reason);
    }

    @Test
    public void test() {
        when(lobbyManager.getLobby(data.id)).thenReturn(lobby);
        socketEvents.onStartup(socket, data, ackRequest);

        verify(lobby, times(1)).initGame(10);
        verify(lobby, times(1)).sendEventToLobby("setupStarted");
    }
}

package com.bs.epic.battleships.unit.sockets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.bs.epic.battleships.SocketEvents;
import com.bs.epic.battleships.events.ErrorEvent;
import com.bs.epic.battleships.events.Uid;
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

public class OnGetSetupDataTest {
    private SocketEvents socketEvents;

    private UserManager userManager = mock(UserManager.class);
    private LobbyManager lobbyManager = mock(LobbyManager.class);

    private SocketIOClient socketOne = mock(SocketIOClient.class);
    private SocketIOClient socketTwo = mock(SocketIOClient.class);

    private MessageService messageService = mock(MessageService.class);

    private AckRequest ackRequest = mock(AckRequest.class);

    private Player playerOne = new Player("Player1", socketOne, "Code1");
    private Player playerTwo = new Player("Player2", socketTwo, "Code2");

    private Lobby lobby = new Lobby(5, playerOne, playerTwo);

    @BeforeEach
    public void beforeEach() {
        socketEvents = new SocketEvents(null, userManager, lobbyManager, null, null, messageService);
        lobby.initGame(10);
    }

    @Test
    public void testUserIsNull() {
        var errorCaptor = ArgumentCaptor.forClass(ErrorEvent.class);
        var data = new Uid(playerOne.uid);

        socketEvents.onGetSetupData(socketOne, data, ackRequest);
        verify(socketOne).sendEvent(eq("errorEvent"), errorCaptor.capture());

        var error = errorCaptor.getValue();
        assertEquals("Invalid user.", error.reason);
    }

    @Test
    public void testLobbyIsNull() {
        var errorCaptor = ArgumentCaptor.forClass(ErrorEvent.class);
        var data = new Uid(playerOne.uid);

        when(userManager.getUser(playerOne.uid)).thenReturn(playerOne);

        socketEvents.onGetSetupData(socketOne, data, ackRequest);
        verify(socketOne).sendEvent(eq("errorEvent"), errorCaptor.capture());

        var error = errorCaptor.getValue();
        assertEquals("Invalid lobby.", error.reason);
    }

    @Test
    public void test() {
        var data = new Uid(playerOne.uid);

        when(userManager.getUser(playerOne.uid)).thenReturn(playerOne);
        when(lobbyManager.getLobbyByUid(playerOne.uid)).thenReturn(lobby);

        socketEvents.onGetSetupData(socketOne, data, ackRequest);

        verify(socketOne, times(1)).sendEvent(eq("setupData"), any());
    }
}

package com.bs.epic.battleships.unit.sockets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.bs.epic.battleships.SocketEvents;
import com.bs.epic.battleships.events.ErrorEvent;
import com.bs.epic.battleships.events.Uid;
import com.bs.epic.battleships.lobby.LobbyManager;
import com.bs.epic.battleships.rest.service.MessageService;
import com.bs.epic.battleships.user.UserManager;
import com.bs.epic.battleships.user.player.Player;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

public class OnGetNameDataTest {
    private SocketEvents socketEvents;

    private UserManager userManager = mock(UserManager.class);
    private LobbyManager lobbyManager = mock(LobbyManager.class);

    private SocketIOClient socket = mock(SocketIOClient.class);

    private MessageService messageService = mock(MessageService.class);

    private AckRequest ackRequest = mock(AckRequest.class);

    private Player player = new Player("Player1", socket, "Code1");

    @BeforeEach
    public void beforeEach() {
        socketEvents = new SocketEvents(null, userManager, lobbyManager, null, null, messageService);
    }

    @Test
    public void testUserIsNull() {
        var errorCaptor = ArgumentCaptor.forClass(ErrorEvent.class);
        var data = new Uid(player.uid);

        socketEvents.onGetNameData(socket, data, ackRequest);
        verify(socket).sendEvent(eq("errorEvent"), errorCaptor.capture());

        var error = errorCaptor.getValue();
        assertEquals("Invalid user.", error.reason);
    }

    @Test
    public void test() {
        var data = new Uid(player.uid);

        when(userManager.getUser(player.uid)).thenReturn(player);
        socketEvents.onGetNameData(socket, data, ackRequest);

        verify(socket, times(1)).sendEvent(eq("nameData"), any());
    }
}

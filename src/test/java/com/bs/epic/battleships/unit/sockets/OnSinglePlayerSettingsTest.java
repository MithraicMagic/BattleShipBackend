package com.bs.epic.battleships.unit.sockets;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import com.bs.epic.battleships.SocketEvents;
import com.bs.epic.battleships.events.ErrorEvent;
import com.bs.epic.battleships.events.Uid;
import com.bs.epic.battleships.lobby.LobbyManager;
import com.bs.epic.battleships.user.UserManager;
import com.bs.epic.battleships.user.player.Player;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

public class OnSinglePlayerSettingsTest {
    private SocketEvents socketEvents;

    private UserManager userManager = mock(UserManager.class);
    private LobbyManager lobbyManager = mock(LobbyManager.class);

    private SocketIOClient socket = mock(SocketIOClient.class);

    private AckRequest ackRequest = mock(AckRequest.class);

    private Uid uid = new Uid("UID");
    private Player player = new Player("Name", socket, "Code");

    @BeforeEach
    public void beforeEach() {
        socketEvents = new SocketEvents(null, userManager, lobbyManager, null, null, null);
    }

    @Test
    public void testPlayerNull() {
        var errorCaptor = ArgumentCaptor.forClass(ErrorEvent.class);
        when(userManager.getPlayer(uid.uid)).thenReturn(null);

        socketEvents.onSinglePlayerSettings(socket, uid, ackRequest);
        verify(socket).sendEvent(eq("errorEvent"), errorCaptor.capture());

        var error = errorCaptor.getValue();
        assertEquals("Invalid player.", error.reason);
    }

    @Test
    public void test() {
        when(userManager.getPlayer(uid.uid)).thenReturn(player);

        socketEvents.onSinglePlayerSettings(socket, uid, ackRequest);
        verify(socket, never()).sendEvent(eq("errorEvent"), any());
    }
}
package com.bs.epic.battleships.unit.sockets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.bs.epic.battleships.SocketEvents;
import com.bs.epic.battleships.events.Uid;
import com.bs.epic.battleships.lobby.Lobby;
import com.bs.epic.battleships.lobby.LobbyManager;
import com.bs.epic.battleships.user.UserManager;
import com.bs.epic.battleships.user.player.Player;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class OnLastUidTest {
    private SocketEvents socketEvents;

    private UserManager userManager = mock(UserManager.class);
    private LobbyManager lobbyManager = mock(LobbyManager.class);

    private SocketIOClient socket = mock(SocketIOClient.class);
    private SocketIOClient socket2 = mock(SocketIOClient.class);

    private AckRequest ackRequest = mock(AckRequest.class);

    private Uid uid = new Uid("UID");
    private Player user = new Player("Name", socket, "Code");
    private Player playerTwo = new Player("Player2", socket2, "Code2");

    private Lobby lobby = new Lobby(5, user, playerTwo);

    @BeforeEach
    public void beforeEach() {
        socketEvents = new SocketEvents(null, userManager, lobbyManager, null, null, null);
    }

    @Test
    public void testUidNull() {
        socketEvents.onLastUid(socket, uid, ackRequest);
        verify(userManager, times(1)).add(any());
    }

    @Test
    public void testReconnectingPlayerWithNullLobby() {
        when(userManager.getUser("UID")).thenReturn(user);
        socketEvents.onLastUid(socket, uid, ackRequest);

        verify(socket, times(1)).sendEvent(eq("reconnect"), any());
    }

    @Test
    public void testReconnectingPlayerWithNonNullLobby() {
        when(userManager.getUser("UID")).thenReturn(user);
        when(lobbyManager.getLobbyByUid("UID")).thenReturn(lobby);

        socketEvents.onLastUid(socket, uid, ackRequest);

        verify(socket, times(1)).sendEvent(eq("reconnectLobby"), any());
        verify(socket2, times(1)).sendEvent("opponentReconnected");
    }
}

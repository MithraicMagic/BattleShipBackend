package com.bs.epic.battleships.unit.sockets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.junit.Assert.*;

import com.bs.epic.battleships.SocketEvents;
import com.bs.epic.battleships.events.ErrorEvent;
import com.bs.epic.battleships.events.Name;
import com.bs.epic.battleships.events.Uid;
import com.bs.epic.battleships.lobby.Lobby;
import com.bs.epic.battleships.lobby.LobbyManager;
import com.bs.epic.battleships.user.UserManager;
import com.bs.epic.battleships.user.player.Player;
import com.bs.epic.battleships.verification.AuthValidator;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

public class OnInputUsername {
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
        socketEvents = new SocketEvents(null, userManager, lobbyManager, new AuthValidator(), null, null);
    }

    @Test
    public void testIncorrectUsername() {
        var errorCaptor = ArgumentCaptor.forClass(ErrorEvent.class);

        socketEvents.onInputUsername(socket, new Name("a"), ackRequest);
        verify(socket).sendEvent(eq("errorEvent"), errorCaptor.capture());

        var error = errorCaptor.getValue();
        assertEquals("Username is too short", error.reason);
    }

    @Test
    public void testUsernameInUse() {
        var errorCaptor = ArgumentCaptor.forClass(ErrorEvent.class);

        when(userManager.nameExists(any())).thenReturn(true);

        socketEvents.onInputUsername(socket, new Name("RENS"), ackRequest);
        verify(socket).sendEvent(eq("errorEvent"), errorCaptor.capture());

        var error = errorCaptor.getValue();
        assertEquals("This username is already in use", error.reason);
    }
}

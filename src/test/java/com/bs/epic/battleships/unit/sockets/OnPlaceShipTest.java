package com.bs.epic.battleships.unit.sockets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.bs.epic.battleships.SocketEvents;
import com.bs.epic.battleships.events.ErrorEvent;
import com.bs.epic.battleships.events.PlaceShip;
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

public class OnPlaceShipTest {
    private SocketEvents socketEvents;

    private UserManager userManager = mock(UserManager.class);
    private LobbyManager lobbyManager = mock(LobbyManager.class);

    private SocketIOClient socketOne = mock(SocketIOClient.class);
    private SocketIOClient socketTwo = mock(SocketIOClient.class);

    private MessageService messageService = mock(MessageService.class);

    private AckRequest ackRequest = mock(AckRequest.class);

    private Player playerOne = new Player("Player1", socketOne, "Code1");
    private Player playerTwo = new Player("Player2", socketOne, "Code2");

    private Lobby lobby = new Lobby(5, playerOne, playerTwo);

    @BeforeEach
    public void beforeEach() {
        socketEvents = new SocketEvents(null, userManager, lobbyManager, null, null, messageService);
        lobby.initGame(10);
    }

    @Test
    public void testLobbyIsNull() {
        var errorCaptor = ArgumentCaptor.forClass(ErrorEvent.class);
        var data = new PlaceShip(5, 0, 0, "UID", "carrier", true);

        socketEvents.onPlaceShip(socketOne, data, ackRequest);
        verify(socketOne).sendEvent(eq("errorEvent"), errorCaptor.capture());

        var error = errorCaptor.getValue();
        assertEquals("Invalid lobby.", error.reason);
    }

    @Test
    public void testPlaceOutsideGrid() {
        var errorCaptor = ArgumentCaptor.forClass(ErrorEvent.class);
        var data = new PlaceShip(5, 0, 0, playerOne.uid, "carrier", true);

        when(lobbyManager.getLobby(5)).thenReturn(lobby);

        socketEvents.onPlaceShip(socketOne, data, ackRequest);
        verify(socketOne).sendEvent(eq("errorEvent"), errorCaptor.capture());

        var error = errorCaptor.getValue();
        assertEquals("You can't place the ship outside of your grid", error.reason);
    }

    @Test
    public void test() {
        var data = new PlaceShip(5, 5, 5, playerOne.uid, "carrier", true);

        when(lobbyManager.getLobby(5)).thenReturn(lobby);

        socketEvents.onPlaceShip(socketOne, data, ackRequest);
        verify(socketOne, never()).sendEvent(eq("errorEvent"), any());
    }
}

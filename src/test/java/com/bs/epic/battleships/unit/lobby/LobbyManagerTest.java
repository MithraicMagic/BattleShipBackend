package com.bs.epic.battleships.unit.lobby;

import com.bs.epic.battleships.lobby.Lobby;
import com.bs.epic.battleships.lobby.LobbyManager;
import com.bs.epic.battleships.user.player.Player;
import com.corundumstudio.socketio.SocketIOClient;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class LobbyManagerTest {
    LobbyManager lobbyManager = new LobbyManager();

    @Test
    public void testAddLobby() {
        Lobby lobby = mock(Lobby.class);
        lobbyManager.add(lobby);
        assertTrue(lobbyManager.lobbies.contains(lobby));
    }

    @Test
    public void testGetLobbyNotFound() {
        assertNull(lobbyManager.getLobby(1));
    }

    @Test
    public void testGetLobbyFound() {
        Lobby lobby = new Lobby(1, mock(Player.class), mock(Player.class));
        lobbyManager.add(lobby);
        assertEquals(lobby, lobbyManager.getLobby(1));
    }

    @Test
    public void testRemoveLobby() {
        Lobby lobby = mock(Lobby.class);
        lobbyManager.add(lobby);
        assertTrue(lobbyManager.lobbies.contains(lobby));
        lobbyManager.remove(lobby);
        assertFalse(lobbyManager.lobbies.contains(lobby));
        verify(lobby, times(1)).clearPlayers();
    }

    @Test
    public void testGetLobbyBySocketNotFound() {
        lobbyManager.add(new Lobby(1, mock(Player.class), mock(Player.class)));
        assertNull(lobbyManager.getLobbyBySocket(mock(SocketIOClient.class)));
    }

    @Test
    public void testGetLobbyBySocketFound() {
        Player playerOne = mock(Player.class);
        SocketIOClient socketOne = mock(SocketIOClient.class);
        playerOne.socket = socketOne;
        Lobby lobby = new Lobby(1, playerOne, mock(Player.class));

        lobbyManager.add(lobby);
        assertEquals(lobby, lobbyManager.getLobbyBySocket(socketOne));
    }

    @Test
    public void testGetLobbyByUidNotFound() {
        lobbyManager.add(new Lobby(1, mock(Player.class), mock(Player.class)));
        assertNull(lobbyManager.getLobbyByUid("playerOne"));
    }

    @Test
    public void testGetLobbyByUidFound() {
        Player playerOne = mock(Player.class);
        playerOne.uid = "playerOne";
        Lobby lobby = new Lobby(1, playerOne, mock(Player.class));
        when(playerOne.isEqual("playerOne")).thenReturn(true);

        lobbyManager.add(lobby);
        assertEquals(lobby, lobbyManager.getLobbyByUid("playerOne"));
    }
}

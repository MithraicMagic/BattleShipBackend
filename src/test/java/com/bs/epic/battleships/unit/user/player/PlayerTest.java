package com.bs.epic.battleships.unit.user.player;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import com.bs.epic.battleships.game.Game;
import com.bs.epic.battleships.user.UserType;
import com.bs.epic.battleships.user.player.Player;
import com.corundumstudio.socketio.SocketIOClient;
import org.junit.jupiter.api.Test;

public class PlayerTest {
    @Test
    public void testReconnect() {
        SocketIOClient newSocket = mock(SocketIOClient.class);
        Player player = new Player("Hans", mock(SocketIOClient.class), "FFFFF", UserType.Player);
        Thread thread = mock(Thread.class);

        player.setThread(thread);
        player.setReconnecting();
        player.onReconnect(newSocket);

        assertEquals(newSocket, player.socket);
        verify(thread, times(1)).start();
        verify(thread, times(1)).interrupt();
    }

    @Test
    public void testGetShips() {
        Player player = new Player("Hans", mock(SocketIOClient.class), "FFFFF");
        Game game = new Game(10);
        game.init(player, player);
        assertTrue(player.getShips().isEmpty());
    }
}

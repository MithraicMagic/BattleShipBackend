package com.bs.epic.battleships.unit.lobby;

import com.bs.epic.battleships.lobby.Lobby;
import com.bs.epic.battleships.user.player.Player;
import com.bs.epic.battleships.util.Util;
import com.corundumstudio.socketio.SocketIOClient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GetPlayerTest {
    private static Lobby lobby;
    private static Player one, two;

    @BeforeAll
    public static void beforeAll() {
        one = new Player("Rens", Mockito.mock(SocketIOClient.class), Util.generateNewCode(5));
        two = new Player("Bert", Mockito.mock(SocketIOClient.class), Util.generateNewCode(5));
    }

    @BeforeEach
    public void beforeEach() {
        lobby = new Lobby(1, one, two);
    }

    @Test
    public void testGetOtherPlayer() {
        var p = lobby.getOtherPlayer(two);
        var p2 = lobby.getOtherPlayer(one);

        assertEquals(p, one);
        assertEquals(p2, two);
    }

    @Test
    public void testGetOtherPlayerByUid() {
        var p = lobby.getOtherPlayer(two.uid);
        var p2 = lobby.getOtherPlayer(one.uid);

        assertEquals(p, one);
        assertEquals(p2, two);
    }

    @Test
    public void testGetPlayerByUid() {
        var p = lobby.getPlayer(one.uid);
        assertEquals(one, p);
    }
}

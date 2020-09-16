package com.bs.epic.battleships.unit.lobby;

import com.bs.epic.battleships.lobby.Lobby;
import com.bs.epic.battleships.stubs.StubSocket;
import com.bs.epic.battleships.user.UserState;
import com.bs.epic.battleships.user.player.Player;
import com.bs.epic.battleships.util.Util;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class OnRematchTest {
    private static Lobby lobby;
    private static Player one, two;

    @BeforeAll
    public static void beforeAll() {
        one = new Player("Rens", new StubSocket(), Util.generateNewCode(5));
        two = new Player("Bert", new StubSocket(), Util.generateNewCode(5));
    }

    @BeforeEach
    public void beforeEach() {
        lobby = new Lobby(1, one, two);
    }

    @Test
    public void testOnRematchOnePlayer() {
        lobby.initGame(10);

        lobby.onRematchRequest(one);

        assertEquals(UserState.Rematch, one.state);
        assertNotEquals(UserState.Rematch, two.state);
    }

    @Test
    public void testOnRematchBothPlayers() {
        lobby.initGame(10);

        lobby.onRematchRequest(one);
        lobby.onRematchRequest(two);

        assertEquals(UserState.Setup, one.state);
        assertEquals(UserState.Setup, two.state);
    }
}

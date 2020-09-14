package com.bs.epic.battleships.unit.lobby;

import com.bs.epic.battleships.lobby.Lobby;
import com.bs.epic.battleships.stubs.StubSocket;
import com.bs.epic.battleships.user.player.Player;
import com.bs.epic.battleships.util.Util;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SendMessageTest {
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
    public void testSendMessage() {
        var result = lobby.sendMessage("Test", one);
        assertTrue(result.success);
    }

    @Test
    public void testSendMessageEmpty() {
        var result = lobby.sendMessage("", one);
        assertFalse(result.success);
    }

    @Test
    public void testSendMessageTooShort() {
        var result = lobby.sendMessage("T", one);
        assertFalse(result.success);
    }

    @Test
    public void testSendMessageTooLong() {
        var result = lobby.sendMessage("A".repeat(105), one);
        assertFalse(result.success);
    }
}

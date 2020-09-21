package com.bs.epic.battleships.unit.lobby;

import com.bs.epic.battleships.lobby.Lobby;
import com.bs.epic.battleships.user.UserState;
import com.bs.epic.battleships.user.player.Player;
import com.bs.epic.battleships.util.Util;
import com.corundumstudio.socketio.SocketIOClient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SwitchTurnTest {
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
    public void test() {
        one.setState(UserState.YourTurn);
        two.setState(UserState.OpponentTurn);

        lobby.switchTurn();

        assertEquals(UserState.OpponentTurn, one.state);
        assertEquals(UserState.YourTurn, two.state);
    }
}

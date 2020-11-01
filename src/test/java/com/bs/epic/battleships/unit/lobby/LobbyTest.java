package com.bs.epic.battleships.unit.lobby;

import com.bs.epic.battleships.events.ErrorEvent;
import com.bs.epic.battleships.game.Game;
import com.bs.epic.battleships.game.grid.GridPos;
import com.bs.epic.battleships.lobby.Lobby;
import com.bs.epic.battleships.user.UserState;
import com.bs.epic.battleships.user.player.Player;
import com.bs.epic.battleships.util.result.Result;
import com.corundumstudio.socketio.SocketIOClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class LobbyTest {
    private Player playerOne;
    private Player playerTwo;

    private final SocketIOClient playerOneSocket = mock(SocketIOClient.class);
    private final SocketIOClient playerTwoSocket = mock(SocketIOClient.class);

    private final String uidPlayerOne = "playerOne";

    private Game game;
    private Lobby lobby;

    @BeforeEach
    public void setup() {
        playerOne = mock(Player.class);
        playerTwo = mock(Player.class);
        playerOne.socket = playerOneSocket;
        playerTwo.socket = playerTwoSocket;
        when(playerOne.isEqual(uidPlayerOne)).thenReturn(true);
        when(playerOne.isEqual(playerOne)).thenReturn(true);
        String uidPlayerTwo = "playerTwo";
        when(playerTwo.isEqual(uidPlayerTwo)).thenReturn(true);
        when(playerTwo.isEqual(playerTwo)).thenReturn(true);

        lobby = new Lobby(1, playerOne, playerTwo);
        game = mock(Game.class);
        lobby.game = game;
    }

    @Test
    public void testAutoPlaceShips() {
        lobby.clearPlayers();
        verify(playerOne, times(1)).onLobbyRemoved();
        verify(playerTwo, times(1)).onLobbyRemoved();
    }

    @Test
    public void testShootSuccess() {
        GridPos gridPos = new GridPos(5, 5);
        when(game.shoot(playerOne, playerTwo, gridPos)).thenReturn(new Result(true, null));

        Result res = lobby.shoot(uidPlayerOne, gridPos);

        assertTrue(res.success);
        verify(game, times(1)).shoot(playerOne, playerTwo, gridPos);
    }

    @Test
    public void testShootFail() {
        GridPos gridPos = new GridPos(5, 5);
        ErrorEvent errorEvent = new ErrorEvent("Shot Missed", "The Shot Missed");
        Result result = new Result(false, errorEvent);
        when(game.shoot(playerOne, playerTwo, gridPos)).thenReturn(result);

        Result res = lobby.shoot(uidPlayerOne, gridPos);

        assertEquals(result, res);
        verify(game, times(1)).shoot(playerOne, playerTwo, gridPos);
    }

    @Test
    public void testDonePlacingSuccessNotBothReady() {
        when(game.donePlacing(playerOne)).thenReturn(new Result(true, null));

        Result res = lobby.donePlacing(uidPlayerOne);

        assertTrue(res.success);
        verify(playerOne, times(1)).setState(UserState.SetupComplete);
        verify(playerTwoSocket, times(1)).sendEvent("opponentSubmitted");
    }

    @Test
    public void testDonePlacingSuccessBothReady() {
        when(game.donePlacing(playerOne)).thenReturn(new Result(true, null));
        playerOne.state = UserState.SetupComplete;
        playerTwo.state = UserState.SetupComplete;

        Result res = lobby.donePlacing(uidPlayerOne);

        assertTrue(res.success);

        verify(playerOne, times(1)).setState(UserState.SetupComplete);
        verify(playerOneSocket, times(1)).sendEvent("gameStarted");
        verify(playerTwoSocket, times(1)).sendEvent("gameStarted");
        verify(playerTwoSocket, times(1)).sendEvent("opponentSubmitted");
    }

    @Test
    public void testDonePlacingFail() {
        ErrorEvent errorEvent = new ErrorEvent("Placement Failed", "The Ships cannot be placed");
        Result result = new Result(false, errorEvent);
        when(game.donePlacing(playerOne)).thenReturn(result);

        Result res = lobby.donePlacing(uidPlayerOne);

        assertEquals(result, res);
        verify(playerOne, never()).setState(UserState.SetupComplete);
    }
}

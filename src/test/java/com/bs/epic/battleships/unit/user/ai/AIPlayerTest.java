package com.bs.epic.battleships.unit.user.ai;

import static org.mockito.Mockito.*;

import com.bs.epic.battleships.game.grid.GridPos;
import com.bs.epic.battleships.user.UserState;
import com.bs.epic.battleships.user.ai.AIPlayer;
import com.bs.epic.battleships.user.ai.behaviour.AiBehaviour;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class AIPlayerTest {
    @Test
    public void testOnEvent() {
        AIPlayer aiPlayer = new AIPlayer(0, 1);
        AiBehaviour aiBehaviour = mock(AiBehaviour.class);
        aiPlayer.setBehaviour(aiBehaviour);

        aiPlayer.onEvent("coolEvent", new Object[]{});
        verify(aiBehaviour, never()).onMessageReceived(any(), any());

        aiPlayer.onEvent("messageReceived", new Object[]{});
        verify(aiBehaviour, times(1)).onMessageReceived(any(), any());
    }

    @Test
    public void testSetState() {
        AIPlayer aiPlayer = new AIPlayer(0, 2);
        AiBehaviour aiBehaviour = mock(AiBehaviour.class);
        aiPlayer.setBehaviour(aiBehaviour);

        aiPlayer.setState(UserState.Available);
        verifyNoInteractions(aiBehaviour);

        aiPlayer.setState(UserState.Setup);
        aiPlayer.setState(UserState.YourTurn);
        aiPlayer.setState(UserState.GameLost);
        aiPlayer.setState(UserState.GameWon);

        verify(aiBehaviour, times(1)).onSetup(null, aiPlayer);
        verify(aiBehaviour, times(1)).onYourTurn(eq(null), eq(aiPlayer.uid), any());
        verify(aiBehaviour, times(1)).onGameLost(null, aiPlayer);
        verify(aiBehaviour, times(1)).onGameWon(null, aiPlayer);
    }
}

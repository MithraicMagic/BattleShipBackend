package com.bs.epic.battleships.unit.user.ai.behaviour;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import com.bs.epic.battleships.game.grid.GridPos;
import com.bs.epic.battleships.lobby.Lobby;
import com.bs.epic.battleships.user.ai.behaviour.medium.AiState;
import com.bs.epic.battleships.user.ai.behaviour.medium.MediumBehaviour;
import com.bs.epic.battleships.util.result.ShootSuccess;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class MediumBehaviourTest {
    private MediumBehaviour mediumBehaviour;
    private ArrayList<GridPos> shotPositions;
    private Lobby lobby;

    @BeforeEach
    public void setup() {
        shotPositions = new ArrayList<>();
        lobby = Mockito.mock(Lobby.class);
    }

    @Test
    public void onYourTurnTest() throws InterruptedException {
        mediumBehaviour = new MediumBehaviour(0, new ArrayList<>());
        when(lobby.shoot(any(), any())).thenReturn(
            new ShootSuccess(true, false, GridPos.random(), 5)
        );

        mediumBehaviour.onYourTurn(lobby, "uid", shotPositions);

        Thread.sleep(20);
        Assert.assertEquals(1, shotPositions.size());
        Assert.assertEquals(AiState.FIRST_HIT, mediumBehaviour.getState());
    }

    @Test
    public void onYourTurnMissedTest() throws InterruptedException {
        mediumBehaviour = new MediumBehaviour(0, new ArrayList<>());
        when(lobby.shoot(any(), any())).thenReturn(
                new ShootSuccess(false, false, GridPos.random(), 5)
        );

        mediumBehaviour.onYourTurn(lobby, "uid", shotPositions);

        Thread.sleep(20);
        Assert.assertEquals(1, shotPositions.size());
        Assert.assertEquals(AiState.DEFAULT, mediumBehaviour.getState());
    }

    @Test
    public void onYourTurnAfterHitTest() throws InterruptedException {
        mediumBehaviour = new MediumBehaviour(0, new ArrayList<>());
        when(lobby.shoot(any(), any())).thenReturn(
            new ShootSuccess(true, false, GridPos.random(), 5)
        );

        mediumBehaviour.onYourTurn(lobby, "uid", shotPositions);
        Thread.sleep(50);

        mediumBehaviour.onYourTurn(lobby, "uid", shotPositions);
        Thread.sleep(50);

        Assert.assertEquals(2, shotPositions.size());
        Assert.assertEquals(AiState.MULTI_HIT, mediumBehaviour.getState());
    }

    @Test
    public void onYourTurnMissAfterHitTest() throws InterruptedException {
        mediumBehaviour = new MediumBehaviour(0, new ArrayList<>());
        when(lobby.shoot(any(), any())).thenAnswer(new Answer() {
            private int count = 0;

            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                count++;
                System.out.println(count != 3);
                return new ShootSuccess(count != 3, false, GridPos.random(), 5);
            }
        });

        for (var i = 0; i < 3; i++) {
            mediumBehaviour.onYourTurn(lobby, "uid", shotPositions);
            Thread.sleep(10);
        }

        Assert.assertEquals(3, shotPositions.size());
        Assert.assertEquals(AiState.FIRST_HIT, mediumBehaviour.getState());
    }
}

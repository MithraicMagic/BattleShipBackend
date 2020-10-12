package com.bs.epic.battleships.unit.user.ai.behaviour;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import com.bs.epic.battleships.game.grid.GridPos;
import com.bs.epic.battleships.lobby.Lobby;
import com.bs.epic.battleships.user.ai.behaviour.easy.EasyBehaviour;
import com.bs.epic.battleships.util.result.ShootSuccess;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoFramework;

public class EasyBehaviourTest {
    private EasyBehaviour easyBehaviour;
    private ArrayList<GridPos> shotPositions;

    @BeforeEach
    public void setup() {
        shotPositions = new ArrayList<>();
        easyBehaviour = new EasyBehaviour(0, new ArrayList<>());
    }

    @Test
    public void onYourTurnTest() throws InterruptedException {
        var lobby = Mockito.mock(Lobby.class);
        when(lobby.shoot(any(), any())).thenReturn(new ShootSuccess(false, false, GridPos.random(), 5));

        easyBehaviour.onYourTurn(lobby, "uid", shotPositions);
        Thread.sleep(10);
        Assert.assertEquals(1, shotPositions.size());
    }

    @Test
    public void onYourTurnShootSamePositionTest() throws InterruptedException {
        var lobby = Mockito.mock(Lobby.class);
        when(lobby.shoot(any(), any())).thenReturn(new ShootSuccess(false, false, GridPos.random(), 5));

        try (var gridPos = Mockito.mockStatic(GridPos.class)) {
            gridPos.when(GridPos::random).thenReturn(new GridPos(1, 1));
        }

        easyBehaviour.onYourTurn(lobby, "uid", shotPositions);
        Thread.sleep(10);
        easyBehaviour.onYourTurn(lobby, "uid", shotPositions);
        Thread.sleep(10);

        Assert.assertEquals(1, shotPositions.size());
    }
}

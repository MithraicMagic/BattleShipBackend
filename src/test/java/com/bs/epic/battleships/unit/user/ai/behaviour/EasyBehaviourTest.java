package com.bs.epic.battleships.unit.user.ai.behaviour;

import java.util.ArrayList;

import com.bs.epic.battleships.user.ai.behaviour.easy.EasyBehaviour;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EasyBehaviourTest {
    private EasyBehaviour easyBehaviour;

    @BeforeEach
    public void setup() {
        easyBehaviour = new EasyBehaviour(100, new ArrayList<>());
    }

    @Test
    public void onYourTurnTest() {
        //easyBehaviour.onYourTurn();
        Assert.assertFalse(false);
    }
}

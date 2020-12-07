package com.bs.epic.battleships.unit.game.grid;

import com.bs.epic.battleships.game.grid.Grid;
import com.bs.epic.battleships.game.grid.GridDirection;
import com.bs.epic.battleships.game.grid.GridPos;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

public class GridPosTest {
    @Test
    public void testAdd() {
        GridPos gridPos = new GridPos(10, 10);
        gridPos.add(GridDirection.DOWN);

        Assert.assertEquals(10, gridPos.i);
        Assert.assertEquals(11, gridPos.j);
    }

    @Test
    public void testRandom() {
        GridPos gridPos = GridPos.random();
        Assert.assertTrue(gridPos.i >= 0 && gridPos.i <= 9);
        Assert.assertTrue(gridPos.j >= 0 && gridPos.j <= 9);
    }

    @Test
    public void testFrom() {
        GridPos gridPos = new GridPos(5, 10);
        GridPos gridPosTest = GridPos.from(gridPos);

        Assert.assertEquals(gridPos.i, gridPosTest.i);
        Assert.assertEquals(gridPos.j, gridPosTest.j);
    }

    @Test
    public void testEqualsSameObject() {
        GridPos p = new GridPos(10, 10);
        Assert.assertEquals(p, p);
    }

    @Test
    public void testEqualsNull() {
        GridPos p = new GridPos(10, 10);
        Assert.assertNotEquals(null, p);
    }

    @Test
    public void testEqualsIsEqual() {
        GridPos gridPosOne = new GridPos(10, 10);
        GridPos gridPosTwo = new GridPos(10, 10);
        Assert.assertEquals(gridPosOne, gridPosTwo);
    }

    @Test
    public void testEqualsIsNotEqual() {
        GridPos gridPosOne = new GridPos(10, 10);
        GridPos gridPosTwo = new GridPos(10, 5);
        Assert.assertNotEquals(gridPosOne, gridPosTwo);
    }

    @Test
    public void testHashCode() {
        GridPos gridPos = new GridPos(10, 10);
        Assert.assertTrue(gridPos.hashCode() != 0);
    }
}

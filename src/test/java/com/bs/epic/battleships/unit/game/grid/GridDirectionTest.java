package com.bs.epic.battleships.unit.game.grid;

import com.bs.epic.battleships.game.grid.GridDirection;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

public class GridDirectionTest {
    @Test
    public void testNext() {
        Assert.assertEquals(GridDirection.LEFT.next(), GridDirection.RIGHT);
        Assert.assertEquals(GridDirection.RIGHT.next(), GridDirection.UP);
        Assert.assertEquals(GridDirection.UP.next(), GridDirection.DOWN);
        Assert.assertEquals(GridDirection.DOWN.next(), GridDirection.LEFT);
        Assert.assertEquals(GridDirection.NONE.next(), GridDirection.LEFT);
    }
}

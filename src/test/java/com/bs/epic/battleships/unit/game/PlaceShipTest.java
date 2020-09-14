package com.bs.epic.battleships.unit.game;

import com.bs.epic.battleships.game.Game;
import com.bs.epic.battleships.game.grid.GridPos;
import com.bs.epic.battleships.stubs.StubSocket;
import com.bs.epic.battleships.user.player.Player;
import com.bs.epic.battleships.util.Util;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PlaceShipTest {
    private static Game game;
    private static Player one, two;

    @BeforeAll
    public static void beforeAll() {
        game = new Game(10);

        one = new Player("Rens", new StubSocket(), Util.generateNewCode(5));
        two = new Player("Bert", new StubSocket(), Util.generateNewCode(5));
    }

    @BeforeEach
    public void beforeEach() {
        game.init(one, two);
    }

    @Test
    public void testPlaceShip() {
        var result = game.placeShip(one, "cruiser", new GridPos(1, 0), true);
        assertTrue(result.success);
    }

    @Test
    public void testPlaceShipOutsideGrid() {
        var result = game.placeShip(one, "cruiser", new GridPos(-1, -1), true);
        assertFalse(result.success);
        assertEquals("You can't place the ship outside of your grid", result.getError().reason);
    }

    @Test
    public void testPlaceShipThatDoesntExist() {
        var result = game.placeShip(one, "bert", new GridPos(1, 0), true);

        assertFalse(result.success);
        assertEquals("This ship doesn't exist", result.getError().reason);
    }

    @Test
    public void testPlaceSameShipTwice() {
        var result = game.placeShip(one, "cruiser", new GridPos(1, 0), true);
        game.placeShip(one, "cruiser", new GridPos(8, 3), true);

        assertTrue(result.success);
        assertEquals(1, one.ships.size());
    }

    @Test
    public void testPlaceTwoShipsInTheSameGridCell() {
        var result = game.placeShip(one, "cruiser", new GridPos(4, 4), true);
        var result2 = game.placeShip(one, "submarine", new GridPos(4, 4), true);

        assertTrue(result.success);
        assertFalse(result2.success);
        assertEquals("The ship doesn't fit there", result2.getError().reason);
    }
}
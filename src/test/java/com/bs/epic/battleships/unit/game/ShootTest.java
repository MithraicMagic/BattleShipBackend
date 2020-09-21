package com.bs.epic.battleships.unit.game;

import com.bs.epic.battleships.game.Game;
import com.bs.epic.battleships.game.grid.GridPos;
import com.bs.epic.battleships.user.player.Player;
import com.bs.epic.battleships.util.Util;
import com.bs.epic.battleships.util.result.ShootSuccess;
import com.corundumstudio.socketio.SocketIOClient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class ShootTest {
    private static Game game;
    private static Player one, two;

    @BeforeAll
    public static void beforeAll() {
        game = new Game(10);

        one = new Player("Rens", Mockito.mock(SocketIOClient.class), Util.generateNewCode(5));
        two = new Player("Bert", Mockito.mock(SocketIOClient.class), Util.generateNewCode(5));
    }

    @BeforeEach
    public void beforeEach() {
        game.init(one, two);
    }

    @Test
    public void testShoot() {
        var shootPos = new GridPos(5, 5);

        var shoot = game.shoot(one, two, shootPos);
        assertTrue(shoot.success);
    }

    @Test
    public void testShootOutsideGrid() {
        var shootPos = new GridPos(-1, 11);

        var shoot = game.shoot(one, two, shootPos);
        assertFalse(shoot.success);
    }

    @Test
    public void testShootSameCellTwice() {
        var shootPos = new GridPos(0, 0);

        var shoot = game.shoot(one, two, shootPos);
        var shoot2 = game.shoot(one, two, shootPos);

        assertTrue(shoot.success);
        assertFalse(shoot2.success);
    }

    @Test
    public void testShootWater() {
        var shootPos = new GridPos(0, 0);
        var shoot = (ShootSuccess) game.shoot(one, two, shootPos);

        assertFalse(shoot.result.hitShip);
        assertFalse(shoot.result.destroyedShip);
    }

    @Test
    public void testShootWaterTwice() {
        var shootPos = new GridPos(0, 0);

        var shoot = (ShootSuccess) game.shoot(one, two, shootPos);
        var shoot2 = game.shoot(one, two, shootPos);

        assertTrue(shoot.success);
        assertFalse(shoot.result.hitShip);
        assertFalse(shoot2.success);
        assertEquals("You've already shot this cell", shoot2.getError().reason);
    }

    @Test
    public void testShootShip() {
        game.placeShip(two, "cruiser", new GridPos(1, 0), true);

        var shootPos = new GridPos(0, 0);
        var shoot = (ShootSuccess) game.shoot(one, two, shootPos);

        assertTrue(shoot.result.hitShip);
    }

    @Test
    public void testShootShipTwice() {
        game.placeShip(two, "cruiser", new GridPos(1, 0), true);

        var shootPos = new GridPos(0, 0);
        var shoot = (ShootSuccess) game.shoot(one, two, shootPos);
        var shoot2 = game.shoot(one, two, shootPos);

        assertTrue(shoot.result.hitShip);
        assertFalse(shoot2.success);
        assertEquals("You've already shot this cell", shoot2.getError().reason);
    }
}

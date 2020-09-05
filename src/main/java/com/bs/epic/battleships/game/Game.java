package com.bs.epic.battleships.game;

import com.bs.epic.battleships.Player;
import com.bs.epic.battleships.PlayerState;
import com.bs.epic.battleships.util.result.Error;
import com.bs.epic.battleships.util.result.ShootSuccess;
import com.bs.epic.battleships.util.result.Result;
import com.bs.epic.battleships.util.result.Success;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Game {
    private int size;
    public GameState state;

    private Map<String, Ship> ships = Stream.of(new Object[][]{
        { "AircraftCarrier", new Ship("AircraftCarrier", 5) },
        { "BattleShip", new Ship("BattleShip", 4) },
        { "Cruiser", new Ship("Cruiser", 3) },
        { "Submarine", new Ship("Submarine", 3) },
        { "MineSweeper",  new Ship("MineSweeper", 2) }
    }).collect(Collectors.toMap(data -> (String) data[0], data -> (Ship) data[1]));

    public Game(int size) {
        this.size = size;
    }

    public void init(Player one, Player two) {
        one.cells = new ArrayList<>();
        two.cells = new ArrayList<>();

        one.ships = new HashMap<>();
        two.ships = new HashMap<>();

        for (int j = 0; j < size; j++) {
            for (int i = 0; i < size; i++) {
                one.cells.add(new GridCell());
                two.cells.add(new GridCell());
            }
        }

        one.setState(PlayerState.Setup);
        two.setState(PlayerState.Setup);
    }

    public Result shoot(Player p, int i, int j) {
        if (i < 0 || i > size || j < 0 || j > size) {
            return new Error("shoot", "You can't shoot outside the grid");
        }

        var cell = p.cells.get(coordsToIndex(i, j));
        switch (cell.state) {
            case HitShip:
            case HitWater:
                return new Error("shoot", "You've already shot this cell");
            case Water:
                return new ShootSuccess(false, false);
            default:
                cell.ship.hitPieces++;
                return new ShootSuccess(true, cell.ship.isDestroyed());
        }
    }

    public Result placeShip(Player p, String s, int i, int j, boolean horizontal) {
        if (!ships.containsKey(s)) return new Error("placeShip", "This ship doesn't exist");
        if (p.ships.containsKey(s)) return new Error("placeShip", "You've already placed this ship");

        if (i < 0 || i > size || j < 0 || j > size) {
            return new Error("placeShip", "You can't place the ship outside of your grid");
        }

        var ship = ships.get(s);
        var delta = ship.length / 2;

        if (horizontal) return this.placeShipHorizontal(p, ship, i, j, delta);

        return this.placeShipVertical(p, ship, i, j, delta);
    }

    private Result placeShipHorizontal(Player p, Ship ship, int i, int j, int delta) {
        var left = i - delta;
        var right = i + ship.length % 2 == 0 ? (delta - 1) : delta;

        if (left < 0 || left >= size || right < 0 || right >= size) {
            return new Error("placeShip", "You can't place the ship outside of your grid");
        }

        for (var index = left; index < right; index++) {
            if (p.cells.get(coordsToIndex(index, j)).state != CellState.Water) {
                return new Error("placeShip", "The ship doesn't fit there");
            }
        }

        for (var index = left; index < right; index++) {
            p.cells.get(coordsToIndex(index, j)).state = CellState.Ship;
            p.cells.get(coordsToIndex(index, j)).ship = ship;
        }

        p.ships.put(ship.name, ship);
        return new Success();
    }

    private Result placeShipVertical(Player p, Ship ship, int i, int j, int delta) {
        var top = j - delta;
        var bottom = j + ship.length % 2 == 0 ? (delta - 1) : delta;

        if (top < 0 || top >= size || bottom < 0 || bottom >= size) {
            return new Error("placeShip", "You can't place the ship outside of your grid");
        }

        for (var index = top; index < bottom; index++) {
            if (p.cells.get(coordsToIndex(i, index)).state != CellState.Water) {
                return new Error("placeShip", "The ship doesn't fit there");
            }
        }

        for (var index = top; index < bottom; index++) {
            p.cells.get(coordsToIndex(i, index)).state = CellState.Ship;
            p.cells.get(coordsToIndex(i, index)).ship = ship;
        }

        p.ships.put(ship.name, ship);
        return new Success();
    }

    public Result donePlacing(Player p) {
        if (p.ships.size() != ships.size()) {
            return new Error("donePlacing", "You have not yet placed all your ships");
        }

        p.donePlacing = true;
        return new Success();
    }

    private int coordsToIndex(int i, int j) {
        return i + j * size;
    }
}

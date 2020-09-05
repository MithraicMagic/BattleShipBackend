package com.bs.epic.battleships.game;

import com.bs.epic.battleships.Player;
import com.bs.epic.battleships.util.Error;
import com.bs.epic.battleships.util.Result;
import com.bs.epic.battleships.util.Success;
import javafx.scene.control.Cell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Game {
    private int size;

    private Map<String, Ship> ships = Stream.of(new Object[][]{
        { "VliegdekSchip", new Ship("VliegdekSchip", 5) },
        { "SlagSchip", new Ship("Slagschip", 4) },
        { "Kruiser", new Ship("Kruiser", 3) },
        { "Onderzeeer", new Ship("Onderzeeer", 3) },
        { "MijnenVeger",  new Ship("Mijnenveger", 2) }
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

    private int coordsToIndex(int i, int j) {
        return i + j * size;
    }
}

package com.bs.epic.battleships.game;

import com.bs.epic.battleships.game.grid.GridPos;
import com.bs.epic.battleships.user.player.Player;
import com.bs.epic.battleships.user.UserState;
import com.bs.epic.battleships.util.Util;
import com.bs.epic.battleships.util.result.Error;
import com.bs.epic.battleships.util.result.ShootSuccess;
import com.bs.epic.battleships.util.result.Result;
import com.bs.epic.battleships.util.result.Success;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Game {
    private final int size;
    public GameState state;

    private Player one, two;

    private final Map<String, Ship> ships = new HashMap<>() {{
        put("carrier", new Ship("carrier", 5));
        put("battleship", new Ship("battleship", 4));
        put("cruiser", new Ship("cruiser", 3));
        put("submarine", new Ship("submarine", 3));
        put("minesweeper", new Ship("minesweeper", 2));
    }};

    public Game(int size) {
        this.size = size;
    }

    public void init(Player one, Player two) {
        one.grid.init(size);
        two.grid.init(size);

        one.ships = new HashMap<>();
        two.ships = new HashMap<>();

        one.setState(UserState.Setup);
        two.setState(UserState.Setup);

        one.boatsLeft = 5;
        two.boatsLeft = 5;

        this.one = one;
        this.two = two;
    }

    public Result shoot(Player player, Player opponent, GridPos pos) {
        if (!inBounds(pos)) {
            return new Error("shoot", "You can't shoot outside the grid");
        }

        var cell = opponent.grid.get(pos);
        ShootSuccess res;

        switch (cell.state) {
            case HitShip:
            case HitWater:
                return new Error("shoot", "You've already shot this cell");
            case Water:
                player.misses.add(pos);
                res = new ShootSuccess(false, false, pos, opponent.boatsLeft);
                cell.state = CellState.HitWater;
                break;
            default:
                cell.ship.hitPieces++;
                player.hits.add(pos);
                var shipDestroyed = cell.ship.isDestroyed();
                if (shipDestroyed) opponent.boatsLeft--;
                res = new ShootSuccess(true, shipDestroyed, pos, opponent.boatsLeft);
                cell.state = CellState.HitShip;
                break;
        }

        opponent.socket.sendEvent("gotShot", res.result);
        return res;
    }

    public Result autoPlaceShips(Player p) {
        for (var ship : ships.values()) {
            if (isAlreadyPlaced(ship.name, p.ships.values())) continue;

            var res = autoPlaceShip(p, ship.name);
            while (!res) {
                res = autoPlaceShip(p, ship.name);
            }
        }

        return new Success();
    }

    private boolean autoPlaceShip(Player p, String name) {
        var pos = GridPos.random();
        var horizontal = Util.randomBool();

        return placeShip(p, name, pos, horizontal).success;
    }

    private boolean isAlreadyPlaced(String name, Collection<Ship> ships) {
        for (var s : ships) {
            if (s.name.equals(name)) {
                return true;
            }
        }

        return false;
    }

    public Result placeShip(Player p, String s, GridPos pos, boolean horizontal) {
        if (!ships.containsKey(s)) return new Error("placeShip", "This ship doesn't exist");

        removeShip(s, p);
        if (pos.i < 0 || pos.i > size || pos.j < 0 || pos.j > size) {
            return new Error("placeShip", "You can't place the ship outside of your grid");
        }

        var templateShip = ships.get(s);
        var ship = new Ship(templateShip, pos, horizontal);
        var delta = ship.length / 2;

        if (horizontal) return this.placeShipHorizontal(p, ship, pos, delta);

        return this.placeShipVertical(p, ship, pos, delta);
    }

    private Result placeShipHorizontal(Player p, Ship ship, GridPos pos, int delta) {
        var left = pos.i - getMinDelta(ship, delta);
        var right = pos.i + delta;

        if (!inBounds(left, right)) {
            return new Error("placeShip", "You can't place the ship outside of your grid");
        }

        for (var index = left; index <= right; index++) {
            if (p.grid.get(new GridPos(index, pos.j)).state != CellState.Water) {
                return new Error("placeShip", "The ship doesn't fit there");
            }
        }

        for (var index = left; index <= right; index++) {
            p.grid.get(new GridPos(index, pos.j)).state = CellState.Ship;
            p.grid.get(new GridPos(index, pos.j)).ship = ship;
        }

        p.ships.put(ship.name, ship);
        return new Success();
    }

    private Result placeShipVertical(Player p, Ship ship, GridPos pos, int delta) {
        var top = pos.j - getMinDelta(ship, delta);
        var bottom = pos.j + delta;

        if (!inBounds(top, bottom)) {
            return new Error("placeShip", "You can't place the ship outside of your grid");
        }

        for (var index = top; index <= bottom; index++) {
            if (p.grid.get(new GridPos(pos.i, index)).state != CellState.Water) {
                return new Error("placeShip", "The ship doesn't fit there");
            }
        }

        for (var index = top; index <= bottom; index++) {
            p.grid.get(new GridPos(pos.i, index)).state = CellState.Ship;
            p.grid.get(new GridPos(pos.i, index)).ship = ship;
        }

        p.ships.put(ship.name, ship);
        return new Success();
    }

    public void removeShip(String s, Player p) {
        p.ships.remove(s);
        for (var cell : p.grid.cells()) {
            if (cell.ship != null && cell.ship.name.equals(s)) {
                cell.ship = null;
                cell.state = CellState.Water;
            }
        }
    }

    public boolean checkVictory() {
        if (checkVictory(two.getShips())) {
            one.setState(UserState.GameWon);
            two.setState(UserState.GameLost);
            return true;
        }
        else if (checkVictory(one.getShips())) {
            one.setState(UserState.GameLost);
            two.setState(UserState.GameWon);
            return true;
        }

        return false;
    }

    private boolean checkVictory(Collection<Ship> ships) {
        boolean allDestroyed = true;
        for (var ship : ships) {
            if (!ship.isDestroyed()) allDestroyed = false;
        }

        return allDestroyed;
    }

    public Result donePlacing(Player p) {
        if (p.ships.size() != ships.size()) {
            return new Error("submitSetup", "You have not yet placed all your ships");
        }
        return new Success();
    }

    private boolean inBounds(int min, int max) {
        return min >= 0 && min < size && max >= 0 && max < size;
    }
    private boolean inBounds(GridPos pos) { return inBounds(pos.i, pos.j); }

    private int getMinDelta(Ship ship, int delta) {
        return (ship.length % 2 == 0 ? (delta / 2) : delta);
    }
}

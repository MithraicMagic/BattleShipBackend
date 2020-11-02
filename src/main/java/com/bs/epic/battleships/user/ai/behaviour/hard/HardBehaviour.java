package com.bs.epic.battleships.user.ai.behaviour.hard;

import java.util.ArrayList;
import java.util.List;

import com.bs.epic.battleships.game.CellState;
import com.bs.epic.battleships.game.Ship;
import com.bs.epic.battleships.game.grid.Grid;
import com.bs.epic.battleships.game.grid.GridDirection;
import com.bs.epic.battleships.game.grid.GridPos;
import com.bs.epic.battleships.lobby.Lobby;
import com.bs.epic.battleships.rest.repository.dto.AiMessage;
import com.bs.epic.battleships.user.ai.behaviour.AiState;
import com.bs.epic.battleships.user.ai.behaviour.medium.MediumBehaviour;
import com.bs.epic.battleships.user.player.Player;

public class HardBehaviour extends MediumBehaviour {
    public HardBehaviour(int delay, List<AiMessage> responses) {
        super(delay, responses);
    }

    public GridPos getShootPos(ArrayList<GridPos> shotPositions, Lobby lobby, String uid) {
        GridPos pos = null;
        while (pos == null || shotPositions.contains(pos)) {
            switch (state) {
            case FIRST_HIT:
                direction = direction.next();
                pos = GridPos.from(firstHitPos);
                pos.add(direction);
                break;
            case MULTI_HIT:
                pos = GridPos.from(prevHitPos);
                do {
                    pos.add(direction);
                }
                while (inBounds(pos) && shotPositions.contains(pos));
                break;
            case DEFAULT:
                do {
                    pos = GridPos.random();
                }
                while (!isTacticalRandomSpot(pos, lobby, uid));
                break;
            }

            if (!inBounds(pos)) {
                pos = null;
                state = AiState.FIRST_HIT;
            }
        }

        shotPositions.add(pos);
        return pos;
    }

    private int getSmallestBoatLeftSize(Player self) {
        int smallest = Integer.MAX_VALUE;

        for (var ship : self.ships.values()) {
            var length = ship.length - ship.hitPieces;

            if (!ship.isDestroyed() && length < smallest) {
                smallest = length;
            }
        }

        return smallest;
    }

    private int spaceInDirection(GridPos p, Grid g, GridDirection direction) {
        var pos = GridPos.from(p);
        //Ignore the base tile
        pos.add(direction);

        var space = 0;
        while (inBounds(pos) && (g.get(pos).state == CellState.Water || g.get(pos).state == CellState.Ship)) {
            space++;
            pos.add(direction);
        }

        return space;
    }

    protected boolean isTacticalRandomSpot(GridPos p, Lobby lobby, String uid) {
        var other = lobby.getOtherPlayer(uid);
        int smallestBoatLength = getSmallestBoatLeftSize(other);
        var potentialHits = concurrentHits;
        if (other.grid.get(p).state == CellState.Ship) potentialHits++;

        var horizontalSpace =
            spaceInDirection(p, other.grid, GridDirection.LEFT) +
            spaceInDirection(p, other.grid, GridDirection.RIGHT);

        var verticalSpace =
            spaceInDirection(p, other.grid, GridDirection.UP) +
            spaceInDirection(p, other.grid, GridDirection.DOWN);

        var spaceNeeded = smallestBoatLength - potentialHits;
        System.out.println("SmallestBoatLength: " + smallestBoatLength);
        System.out.println("ConcurrentHits " + potentialHits);
        System.out.println("Space Needed: " + spaceNeeded);
        System.out.println("Horizontal: " + horizontalSpace + " Vertical: " + verticalSpace);

        return horizontalSpace >= spaceNeeded || verticalSpace >= spaceNeeded;
    }
}

package com.bs.epic.battleships.user.ai.behaviour.medium;

import com.bs.epic.battleships.game.grid.GridDirection;
import com.bs.epic.battleships.game.grid.GridPos;
import com.bs.epic.battleships.lobby.Lobby;
import com.bs.epic.battleships.user.ai.behaviour.BaseBehaviour;
import com.bs.epic.battleships.util.result.ShootSuccess;

import java.util.ArrayList;

public class MediumBehaviour extends BaseBehaviour {
    private AiState state;
    private GridPos firstHitPos;
    private GridPos prevHitPos;

    private GridDirection direction;

    public MediumBehaviour(int delay) {
        super(delay);
        this.state = AiState.DEFAULT;
        this.direction = GridDirection.NONE;
    }

    @Override
    public void onYourTurn(Lobby lobby, String uid, ArrayList<GridPos> shotPositions) {
        var t = getTaskThread(() -> {
            var pos = getShootPos(shotPositions);
            var result = lobby.shoot(uid, pos);
            if (!result.success) {
                System.out.println("AI tried to shoot a cell twice");
            }

            if (result.success) {
                var res = (ShootSuccess) result;
                var shotSuccess = res.result;

                if (shotSuccess.hitShip) {
                    if (shotSuccess.destroyedShip) state = AiState.DEFAULT;
                    else {
                        prevHitPos = pos;

                        switch (state) {
                            case DEFAULT:
                                state = AiState.JUST_HIT_SHIP;
                                prevHitPos = pos;
                                firstHitPos = pos;
                                break;
                            case JUST_HIT_SHIP:
                                state = AiState.HIT_SHIP;
                                break;
                        }
                    }
                }
                else if (state == AiState.HIT_SHIP){
                    state = AiState.JUST_HIT_SHIP;
                }
            }
        });
        t.start();
    }

    public GridPos getShootPos(ArrayList<GridPos> shotPositions) {
        GridPos pos = null, hitTried = null;
        while (pos == null || shotPositions.contains(pos)) {
            switch (state) {
                case JUST_HIT_SHIP:
                    direction = direction.next();
                    pos = GridPos.from(firstHitPos);
                    pos = pos.add(direction);
                    break;
                case HIT_SHIP:
                    pos = GridPos.from(prevHitPos);
                    pos = pos.add(direction);
                    while (inBounds(pos) && shotPositions.contains(pos)) pos = pos.add(direction);
                    break;
                case DEFAULT:
                    pos = GridPos.random();
                    break;
            }

            if (!inBounds(pos)) {
                pos = null;
                state = AiState.JUST_HIT_SHIP;
            }
        }

        shotPositions.add(pos);
        return pos;
    }

    private boolean inBounds(GridPos p) {
        return p.i >= 0 && p.i < 10 && p.j >= 0 && p.j < 10;
    }
}

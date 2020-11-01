package com.bs.epic.battleships.user.ai.behaviour.medium;

import com.bs.epic.battleships.game.grid.GridDirection;
import com.bs.epic.battleships.game.grid.GridPos;
import com.bs.epic.battleships.lobby.Lobby;
import com.bs.epic.battleships.rest.repository.dto.AiMessage;
import com.bs.epic.battleships.user.ai.behaviour.AiState;
import com.bs.epic.battleships.user.ai.behaviour.BaseBehaviour;
import com.bs.epic.battleships.util.result.ShootSuccess;

import java.util.ArrayList;
import java.util.List;

public class MediumBehaviour extends BaseBehaviour {
    protected AiState state;
    protected GridPos firstHitPos;
    protected GridPos prevHitPos;

    protected GridDirection direction;
    protected int concurrentHits = 0;

    public MediumBehaviour(int delay, List<AiMessage> responses) {
        super(delay, responses);
        this.state = AiState.DEFAULT;
        this.direction = GridDirection.NONE;
    }

    @Override
    public void onYourTurn(Lobby lobby, String uid, ArrayList<GridPos> shotPositions) {
        var t = getTaskThread(() -> {
            var pos = getShootPos(shotPositions, lobby, uid);
            var result = lobby.shoot(uid, pos);
            if (!result.success) {
                System.out.println("AI tried to shoot a cell twice");
            }

            if (result.success) {
                var res = (ShootSuccess) result;
                var shotSuccess = res.result;

                if (shotSuccess.hitShip) {
                    if (shotSuccess.destroyedShip) {
                        state = AiState.DEFAULT;
                        concurrentHits = 0;
                    }
                    else {
                        prevHitPos = pos;
                        concurrentHits++;

                        switch (state) {
                            case DEFAULT:
                                state = AiState.FIRST_HIT;
                                firstHitPos = pos;
                                break;
                            case FIRST_HIT:
                                state = AiState.MULTI_HIT;
                                break;
                        }
                    }
                }
                else if (state == AiState.MULTI_HIT){
                    state = AiState.FIRST_HIT;
                }
            }
        });
        t.start();
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
                    pos = GridPos.random();
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

    public AiState getState() { return state; }

    protected boolean inBounds(GridPos p) {
        return p.i >= 0 && p.i < 10 && p.j >= 0 && p.j < 10;
    }
}

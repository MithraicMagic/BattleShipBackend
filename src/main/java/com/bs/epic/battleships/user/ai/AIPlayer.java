package com.bs.epic.battleships.user.ai;

import com.bs.epic.battleships.game.grid.GridPos;
import com.bs.epic.battleships.lobby.Lobby;
import com.bs.epic.battleships.user.ai.behaviour.medium.MediumBehaviour;
import com.bs.epic.battleships.user.player.Player;
import com.bs.epic.battleships.user.UserState;
import com.bs.epic.battleships.user.UserType;
import com.bs.epic.battleships.user.ai.behaviour.AiBehaviour;
import com.bs.epic.battleships.user.ai.behaviour.easy.EasyBehaviour;
import com.bs.epic.battleships.util.Util;

import java.util.ArrayList;

public class AIPlayer extends Player {
    public Lobby lobby;

    private final ArrayList<GridPos> shotPositions;

    private AiBehaviour behaviour;

    public AIPlayer(int delay, int difficulty) {
        super("Computer", new StubSocket(), Util.generateNewCode(12), UserType.Ai);

        switch (difficulty) {
            case 2:
                this.behaviour = new MediumBehaviour(delay);
                break;
            case 1:
            case 3:
                this.behaviour = new EasyBehaviour(delay);
                break;
        }

        var s = (StubSocket) this.socket;
        s.setAi(this);

        this.shotPositions = new ArrayList<>();
    }

    public void onEvent(String event, Object[] obj) {
        switch (event) {
            case "messageReceived":
                behaviour.onMessageReceived(lobby, this);
                break;
            default:
                break;
        }
    }

    @Override
    public void setState(UserState state) {
        if (this.state == state) {
            //State didn't change so let's ignore it completely
            return;
        }

        super.setState(state);
        switch (this.state) {
            case Setup:
                behaviour.onSetup(lobby, this);
                break;
            case YourTurn:
                behaviour.onYourTurn(lobby, uid, shotPositions);
                break;
            case GameLost:
                behaviour.onGameLost(lobby, this);
                break;
            case GameWon:
                behaviour.onGameWon(lobby, this);
                break;
        }
    }

    @Override
    public void onLobbyRemoved() {
        super.onLobbyRemoved();

        if (shotPositions != null) shotPositions.clear();
    }
}

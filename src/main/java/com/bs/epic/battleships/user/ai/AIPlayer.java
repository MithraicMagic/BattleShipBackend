package com.bs.epic.battleships.user.ai;

import com.bs.epic.battleships.game.grid.GridPos;
import com.bs.epic.battleships.lobby.Lobby;
import com.bs.epic.battleships.user.player.Player;
import com.bs.epic.battleships.user.UserState;
import com.bs.epic.battleships.user.UserType;
import com.bs.epic.battleships.user.ai.behaviour.AiBehaviour;
import com.bs.epic.battleships.user.ai.behaviour.EasyBehaviour;
import com.bs.epic.battleships.util.Util;

import java.util.ArrayList;

public class AIPlayer extends Player {
    private Player human;
    public Lobby lobby;

    private int decisionDelay;
    private ArrayList<GridPos> shotPositions;

    private AiBehaviour behaviour;

    public AIPlayer(Player human, int delay, int difficulty) {
        super("Computer", new StubSocket(), Util.generateNewCode(12), UserType.Ai);
        this.human = human;
        this.decisionDelay = delay;

        switch (difficulty) {
            case 1:
            case 2:
            case 3:
                this.behaviour = new EasyBehaviour(this.decisionDelay);
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

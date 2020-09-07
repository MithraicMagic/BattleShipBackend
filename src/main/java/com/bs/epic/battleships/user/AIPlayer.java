package com.bs.epic.battleships.user;

import com.bs.epic.battleships.lobby.Lobby;
import com.bs.epic.battleships.util.StubSocket;
import com.bs.epic.battleships.util.Util;

public class AIPlayer extends Player {
    private Player human;
    public Lobby lobby;

    public AIPlayer(Player human) {
        super("Computer", new StubSocket(), Util.generateNewCode(12));
        this.human = human;
    }

    @Override
    public void receiveMessage(String message) {
        super.receiveMessage(message);
        this.sendMessage(human, "NEE STERF");
    }

    @Override
    public void setState(UserState state) {
        super.setState(state);

        switch (state) {
            case Setup:
                lobby.game.autoPlaceShips(this);
                lobby.donePlacing(uid);
                break;
        }
    }
}

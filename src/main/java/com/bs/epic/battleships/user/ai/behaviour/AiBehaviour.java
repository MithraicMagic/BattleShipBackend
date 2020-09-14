package com.bs.epic.battleships.user.ai.behaviour;

import com.bs.epic.battleships.game.grid.GridPos;
import com.bs.epic.battleships.lobby.Lobby;
import com.bs.epic.battleships.user.player.Player;

import java.util.ArrayList;

public interface AiBehaviour {
    void onSetup(Lobby lobby, Player p);

    void onYourTurn(Lobby lobby, String uid, ArrayList<GridPos> shotPositions);
    void onOpponentTurn();

    void onGameLost(Lobby l, Player p);
    void onGameWon(Lobby l, Player p);

    void onMessageReceived(Lobby l, Player p);
}

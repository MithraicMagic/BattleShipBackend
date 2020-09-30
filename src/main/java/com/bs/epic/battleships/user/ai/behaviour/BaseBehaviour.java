package com.bs.epic.battleships.user.ai.behaviour;

import com.bs.epic.battleships.game.grid.GridPos;
import com.bs.epic.battleships.lobby.Lobby;
import com.bs.epic.battleships.rest.controller.MessageController;
import com.bs.epic.battleships.rest.repository.dto.AiMessage;
import com.bs.epic.battleships.rest.service.MessageService;
import com.bs.epic.battleships.user.player.Player;
import com.bs.epic.battleships.util.Util;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseBehaviour implements AiBehaviour {
    private final int delay;
    private final List<AiMessage> responses;

    public BaseBehaviour(int delay) {
        this.delay = delay;
        responses = MessageHandler.getInstance().getAiMessage();
    }

    @Override
    public void onSetup(Lobby lobby, Player player) {
        var t = getTaskThread(() -> {
            lobby.game.autoPlaceShips(player);
            lobby.donePlacing(player.uid);
        });
        t.start();
    }

    public abstract void onYourTurn(Lobby lobby, String uid, ArrayList<GridPos> shotPositions);

    @Override
    public void onGameLost(Lobby lobby, Player p) {
        lobby.sendMessage("Well played.", p);
        lobby.onRematchRequest(p);
    }

    @Override
    public void onGameWon(Lobby lobby, Player p) {
        lobby.sendMessage("OMG EASY GAME.", p);
        lobby.onRematchRequest(p);
    }

    @Override
    public void onMessageReceived(Lobby lobby, Player p) {
        var index = Util.randomInt(0, responses.size() - 1);
        lobby.sendMessage(responses.get(index).text, p);
    }

    protected Thread getTaskThread(Task task) {
        return new Thread(() -> {
            try {
                Thread.sleep(delay);
                task.execute();
            } catch (InterruptedException ignored) { }
        });
    }
}

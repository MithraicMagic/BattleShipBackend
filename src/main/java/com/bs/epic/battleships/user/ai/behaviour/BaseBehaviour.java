package com.bs.epic.battleships.user.ai.behaviour;

import com.bs.epic.battleships.game.grid.GridPos;
import com.bs.epic.battleships.lobby.Lobby;
import com.bs.epic.battleships.user.player.Player;
import com.bs.epic.battleships.util.Util;

import java.util.ArrayList;

public abstract class BaseBehaviour implements AiBehaviour {
    private final int delay;

    private final ArrayList<String> responses = new ArrayList<>() {{
        add("WOw yOu SuCK"); add("You are so slow"); add("This is extremely easy");
        add("I'm getting bored"); add("This game is VERY easy"); add("Honestly I'm not even trying");
        add("How old are you? Like 12?"); add("Are you even trying?"); add("Even my dog is better than you");
        add("Zzzzzzzzz"); add("HaHAhAHAhahaHa"); add("OmegaLUL"); add("Watching you play is just sad tbh");
    }};

    public BaseBehaviour(int delay) {
        this.delay = delay;
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
        lobby.sendMessage(responses.get(index), p);
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

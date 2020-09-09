package com.bs.epic.battleships.user.ai;

import com.bs.epic.battleships.game.GridPos;
import com.bs.epic.battleships.lobby.Lobby;
import com.bs.epic.battleships.user.Player;
import com.bs.epic.battleships.user.UserState;
import com.bs.epic.battleships.user.UserType;
import com.bs.epic.battleships.util.Util;

import java.util.ArrayList;
import java.util.function.Function;

public class AIPlayer extends Player {
    private Player human;
    public Lobby lobby;
    private ArrayList<GridPos> shotPositions;

    private int decisionDelay;
    private int difficulty;

    private final ArrayList<String> responses = new ArrayList<>() {{
        add("WOw yOu SuCK"); add("You are so slow"); add("This is extremely easy");
        add("I'm getting bored"); add("This game is VERY easy"); add("Honestly I'm not even trying");
        add("How old are you? Like 12?"); add("Are you even trying?"); add("Even my dog is better than you");
        add("Zzzzzzzzz"); add("HaHAhAHAhahaHa");
    }};

    public AIPlayer(Player human, int delay, int difficulty) {
        super("Computer", new StubSocket(), Util.generateNewCode(12), UserType.Ai);
        this.human = human;
        this.decisionDelay = delay;
        this.difficulty = difficulty;

        var s = (StubSocket) this.socket;
        s.setAi(this);

        this.shotPositions = new ArrayList<>();
    }

    public void onEvent(String event, Object[] obj) {
        switch (event) {
            case "messageReceived":
                var index = Util.randomInt(0, responses.size() - 1);
                lobby.sendMessage(responses.get(index), this);
                break;
            case "otherRematch":
                lobby.onRematchRequest(this);
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
                var setupTask = getTaskThread((uid) -> {
                    lobby.game.autoPlaceShips(this);
                    lobby.donePlacing(uid);
                    return true;
                });
                setupTask.start();
                break;
            case YourTurn:
                var t = getTaskThread((uid) -> {
                    var pos = GridPos.random();
                    while (shotPositions.contains(pos)) {
                        pos = GridPos.random();
                    }

                    shotPositions.add(pos);
                    var result = lobby.shoot(uid, pos);
                    if (!result.success) {
                        System.out.println("AI tried to shoot a cell twice");
                    }
                    return true;
                });
                t.start();
                break;
            case GameLost:
                lobby.sendMessage("Well played.", this);
                break;
            case GameWon:
                lobby.sendMessage("OMG EASY GAME.", this);
                break;
        }
    }

    @Override
    public void onLobbyRemoved() {
        super.onLobbyRemoved();

        if (shotPositions != null) shotPositions.clear();
    }

    private Thread getTaskThread(Function<String, Boolean> func) {
        return new Thread(() -> {
            try {
                Thread.sleep(this.decisionDelay);
                func.apply(uid);
            } catch (InterruptedException ignored) { }
        });
    }
}

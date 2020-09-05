package com.bs.epic.battleships;

import com.bs.epic.battleships.events.LobbyJoined;
import com.bs.epic.battleships.game.Game;
import com.bs.epic.battleships.util.result.Result;

public class Lobby {
    public int id;
    public Game game;

    public Player playerOne;
    public Player playerTwo;

    public Thread disconnectThreadOne;
    public Thread disconnectThreadTwo;

    public Lobby(int id, Player playerOne, Player playerTwo) {
        this.id = id;
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
    }

    public void addThread(Thread t1, Thread t2) {
        this.disconnectThreadOne = t1;
        this.disconnectThreadTwo = t2;
    }

    public Result shoot(String uid, int i, int j) {
        var res = playerOne.UID == uid ? game.shoot(playerOne, i, j) : game.shoot(playerTwo, i, j);
        if (res.success) this.switchTurn();
        return res;
    }

    public void switchTurn() {
        if (playerOne.state == PlayerState.YourTurn) {
            playerOne.state = PlayerState.OpponentTurn;
            playerTwo.state = PlayerState.YourTurn;

            playerOne.socket.sendEvent("opponentTurn");
            playerTwo.socket.sendEvent("yourTurn");
        }
        else {
            playerOne.state = PlayerState.YourTurn;
            playerTwo.state = PlayerState.OpponentTurn;

            playerOne.socket.sendEvent("yourTurn");
            playerTwo.socket.sendEvent("opponentTurn");
        }
    }

    public Player getPlayer(String uid) {
        return playerOne.isEqual(uid) ? playerOne : playerTwo;
    }

    public void sendEventToLobby(String event, Object o) {
        if (playerOne != null) {
            playerOne.socket.sendEvent(event, o);
        }
        if (playerTwo != null) {
            playerTwo.socket.sendEvent(event, o);
        }
    }

    public void sendLobbyJoinedEvent() {
        if (playerOne != null) playerOne.socket.sendEvent("lobbyJoined", new LobbyJoined(id, playerTwo.name, true));
        if (playerTwo != null) playerTwo.socket.sendEvent("lobbyJoined", new LobbyJoined(id, playerOne.name, false));
    }
}

package com.bs.epic.battleships;

import com.bs.epic.battleships.events.LobbyJoined;
import com.bs.epic.battleships.game.Game;

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

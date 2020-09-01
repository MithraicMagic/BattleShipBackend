package com.bs.epic.battleships;

import com.corundumstudio.socketio.SocketIOClient;

public class Lobby {
    public int Id;
    public String code;
    public Player playerOne;
    public Player playerTwo;

    public Lobby(int Id, String code, SocketIOClient playerSocket) {
        this.Id = Id;
        this.code = code;
        this.playerOne = new Player("Freek", playerSocket);
    }

    public void addPlayer(SocketIOClient newPlayer) {
        this.playerTwo = new Player("Frank", newPlayer);
    }

    public void sendEventToLobby(String event, Object o) {
        playerOne.socket.sendEvent(event, o);
        if (playerTwo != null) {
            playerTwo.socket.sendEvent(event, o);
        }
    }
}

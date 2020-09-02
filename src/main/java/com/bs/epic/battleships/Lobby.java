package com.bs.epic.battleships;

import com.corundumstudio.socketio.SocketIOClient;

public class Lobby {
    public int Id;
    public String code;
    public Player playerOne;
    public Player playerTwo;

    public Lobby(int Id, Player playerOne, Player playerTwo) {
        this.Id = Id;
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
    }

    public void sendEventToLobby(String event, Object o) {
        if (playerOne != null) {
            playerOne.socket.sendEvent(event, o);
        }
        if (playerTwo != null) {
            playerTwo.socket.sendEvent(event, o);
        }
    }
}

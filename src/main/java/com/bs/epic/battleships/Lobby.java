package com.bs.epic.battleships;

public class Lobby {
    public int Id;
    public Player playerOne;
    public Player playerTwo;
    public Thread disconnectThreadOne;
    public Thread disconnectThreadTwo;

    public Lobby(int Id, Player playerOne, Player playerTwo) {
        this.Id = Id;
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
}

package com.bs.epic.battleships.lobby;

import com.corundumstudio.socketio.SocketIOClient;

import java.util.ArrayList;

public class LobbyManager {
    public ArrayList<Lobby> lobbies;

    public LobbyManager() {
        lobbies = new ArrayList<>();
    }

    public Lobby getLobby(int lobbyId) {
        for (var l : lobbies) if (l.id == lobbyId) return l;
        return null;
    }

    public void add(Lobby lobby) {
        System.out.println("Adding lobby with id: " + lobby.id);
        lobbies.add(lobby);
    }

    public void remove(Lobby lobby) {
        System.out.println("Removing lobby with id: " + lobby.id);
        lobby.clearPlayers();
        lobbies.remove(lobby);
    }

    public Lobby getLobbyBySocket(SocketIOClient s) {
        for (var l : lobbies) if (l.playerOne.socket == s || l.playerTwo.socket == s) return l;
        return null;
    }

    public Lobby getLobbyByUid(String uid) {
        for (var l : lobbies) if (l.playerOne.isEqual(uid) || l.playerTwo.isEqual(uid)) return l;
        return null;
    }
}

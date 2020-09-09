package com.bs.epic.battleships.user;

public enum UserState {
    None, EnterName, Available, Lobby, Settings, Setup, SetupComplete, YourTurn, OpponentTurn,
    Reconnecting, OpponentReconnecting, GameWon, GameLost, Rematch
}

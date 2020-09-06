package com.bs.epic.battleships.user;

import com.corundumstudio.socketio.SocketIOClient;

import java.util.UUID;

public class User {
    public UserType type;
    public String uid;

    public UserState state;
    public UserState prevState;

    public SocketIOClient socket;
    protected Thread disconnectThread;

    public User(SocketIOClient socket) {
        this.socket = socket;
        this.uid = UUID.randomUUID().toString();

        this.state = UserState.EnterName;
        this.prevState = UserState.EnterName;

        type = UserType.User;
    }

    public void setState(UserState state) {
        if (this.state == UserState.OpponentReconnecting && state == UserState.Reconnecting) {
            this.state = UserState.Reconnecting;
        }
        else {
            prevState = this.state;
            this.state = state;
        }

        if (state != UserState.Reconnecting) socket.sendEvent("playerState", this.state.toString());
    }

    public void revertState() {
        this.setState(this.prevState);
    }

    public boolean isEqual(String uid) {
        return this.uid.equals(uid);
    }
    public boolean isEqual(Player p) { return uid.equals(p.uid); }
}

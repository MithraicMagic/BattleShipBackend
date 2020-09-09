package com.bs.epic.battleships.user.ai;

import com.corundumstudio.socketio.*;
import com.corundumstudio.socketio.protocol.Packet;

import java.net.SocketAddress;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

public class StubSocket implements SocketIOClient {
    private AIPlayer ai;

    public void setAi(AIPlayer ai) {
        this.ai = ai;
    }

    @Override
    public HandshakeData getHandshakeData() {
        return null;
    }

    @Override
    public Transport getTransport() {
        return null;
    }

    @Override
    public void sendEvent(String s, AckCallback<?> ackCallback, Object... objects) {
    }

    @Override
    public void send(Packet packet, AckCallback<?> ackCallback) { }

    @Override
    public SocketIONamespace getNamespace() {
        return null;
    }

    @Override
    public UUID getSessionId() {
        return null;
    }

    @Override
    public SocketAddress getRemoteAddress() {
        return null;
    }

    @Override
    public boolean isChannelOpen() {
        return true;
    }

    @Override
    public void joinRoom(String s) { }

    @Override
    public void leaveRoom(String s) { }

    @Override
    public Set<String> getAllRooms() {
        return null;
    }

    @Override
    public void send(Packet packet) { }

    @Override
    public void disconnect() { }

    @Override
    public void sendEvent(String s, Object... objects) {
        if (ai != null) ai.onEvent(s, Arrays.stream(objects).toArray());
    }

    @Override
    public void set(String s, Object o) { }

    @Override
    public <T> T get(String s) {
        return null;
    }

    @Override
    public boolean has(String s) {
        return false;
    }

    @Override
    public void del(String s) {
    }
}

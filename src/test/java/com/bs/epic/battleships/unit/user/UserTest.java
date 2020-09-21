package com.bs.epic.battleships.unit.user;

import com.bs.epic.battleships.events.State;
import com.bs.epic.battleships.user.User;
import com.bs.epic.battleships.user.UserState;
import com.bs.epic.battleships.user.player.Player;
import com.corundumstudio.socketio.SocketIOClient;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

public class UserTest {
    private SocketIOClient socketIOClient;
    private User user;

    @BeforeEach
    public void setup() {
        socketIOClient = mock(SocketIOClient.class);
        user = new User(socketIOClient);
    }

    @Test
    public void testSetStateSameState() {
        user.setState(UserState.EnterName);

        Assert.assertEquals(UserState.EnterName, user.state);
        Assert.assertEquals(UserState.EnterName, user.prevState);
        verify(socketIOClient, times(1)).sendEvent(eq("playerState"), any(State.class));
    }

    @Test
    public void testSetStateNotSameState() {
        user.setState(UserState.YourTurn);

        Assert.assertEquals(UserState.YourTurn, user.state);
        Assert.assertEquals(UserState.YourTurn, user.prevState);
        verify(socketIOClient, times(1)).sendEvent(eq("playerState"), any(State.class));
    }

    @Test
    public void testSetStateReconnecting() {
        user.setState(UserState.Reconnecting);

        Assert.assertEquals(UserState.Reconnecting, user.state);
        Assert.assertEquals(UserState.EnterName, user.prevState);
        verify(socketIOClient, never()).sendEvent(eq("playerState"), any(State.class));
    }

    @Test
    public void testRevertState() {
        user.setState(UserState.Reconnecting);

        Assert.assertEquals(UserState.Reconnecting, user.state);
        Assert.assertEquals(UserState.EnterName, user.prevState);

        user.revertState();

        Assert.assertEquals(UserState.EnterName, user.state);
        Assert.assertEquals(UserState.EnterName, user.prevState);
    }

    @Test
    public void testIsEqual() {
        Player newUser = new Player("Hans", mock(SocketIOClient.class), "FFFFF");
        Assert.assertTrue(newUser.isEqual(newUser));
        Assert.assertTrue(user.isEqual(user.uid));
    }
}

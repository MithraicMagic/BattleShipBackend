package com.bs.epic.battleships.unit.user;

import com.bs.epic.battleships.user.User;
import com.bs.epic.battleships.user.UserManager;
import com.bs.epic.battleships.user.player.Player;
import com.corundumstudio.socketio.SocketIOClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class UserManagerTest {
    private UserManager userManager;

    @BeforeEach
    public void setup() {
        userManager = new UserManager();
    }

    @Test
    public void testAddUser() {
        User user = new User(mock(SocketIOClient.class));

        assertEquals(0, userManager.getUsers().size());
        assertNull(userManager.getUser(user.uid));

        userManager.add(user);
        assertEquals(1, userManager.getUsers().size());
        assertEquals(user, userManager.getUser(user.uid));
    }

    @Test
    public void testRemoveUser() {
        User user = new User(mock(SocketIOClient.class));

        userManager.add(user);
        assertEquals(1, userManager.getUsers().size());
        assertEquals(user, userManager.getUser(user.uid));

        userManager.remove(user);
        assertEquals(0, userManager.getUsers().size());
        assertNull(userManager.getUser(user.uid));
    }

    @Test
    public void testReplaceUserByPlayer() {
        SocketIOClient socket = mock(SocketIOClient.class);
        User user = new User(socket);
        Player player = new Player("Hans", socket, "FFFFF");

        userManager.add(user);
        assertEquals(user, userManager.getUser(user.uid));
        assertNull(userManager.getPlayer(player.uid));

        userManager.replaceUserByPlayer(player);
        assertNull(userManager.getUser(user.uid));
        assertEquals(player, userManager.getPlayer(player.uid));
    }

    @Test
    public void testNameExists() {
        userManager.add(new Player("Hans", mock(SocketIOClient.class), "FFFFF"));

        assertTrue(userManager.nameExists("Hans"));
        assertFalse(userManager.nameExists("Freek"));
    }

    @Test
    public void testGetUser() {
        User user = new User(mock(SocketIOClient.class));
        userManager.add(user);

        assertNull(userManager.getUser(null));
        assertNull(userManager.getUser("testUid"));
        assertEquals(user, userManager.getUser(user.uid));
    }

    @Test
    public void testGetPlayer() {
        Player player = new Player("Hans", mock(SocketIOClient.class), "FFFFF");
        userManager.add(player);

        assertNull(userManager.getPlayer(null));
        assertNull(userManager.getPlayer("testUid"));
        assertEquals(player, userManager.getPlayer(player.uid));
    }

    @Test
    public void testGetByCode() {
        Player player = new Player("Hans", mock(SocketIOClient.class), "FFFFF");
        userManager.add(player);

        assertNull(userManager.getByCode(null));
        assertNull(userManager.getByCode("testCode"));
        assertEquals(player, userManager.getByCode("FFFFF"));
    }

    @Test
    public void testGetBySocket() {
        User user = new User(mock(SocketIOClient.class));
        userManager.add(user);

        assertNull(userManager.getBySocket(null));
        assertNull(userManager.getBySocket(mock(SocketIOClient.class)));
        assertEquals(user, userManager.getBySocket(user.socket));
    }
}

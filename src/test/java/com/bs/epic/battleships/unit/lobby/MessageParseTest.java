package com.bs.epic.battleships.unit.lobby;

import com.bs.epic.battleships.events.Command;
import com.bs.epic.battleships.lobby.Lobby;
import com.bs.epic.battleships.user.UserState;
import com.bs.epic.battleships.user.player.Player;
import com.corundumstudio.socketio.SocketIOClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class MessageParseTest {
    private Player playerOne;
    private Player playerTwo;
    private SocketIOClient socketOne;
    private SocketIOClient socketTwo;
    private Lobby lobby;

    private ArgumentCaptor<Command> commandArgumentCaptor = ArgumentCaptor.forClass(Command.class);

    @BeforeEach
    public void setup() {
        playerOne = mock(Player.class);
        playerTwo = mock(Player.class);
        socketOne = mock(SocketIOClient.class);
        socketTwo = mock(SocketIOClient.class);

        playerOne.socket = socketOne;
        playerTwo.socket = socketTwo;
        lobby = new Lobby(1, playerOne, playerTwo);
    }

    @Test
    public void testParseMessageNoCommand() {
        assertFalse(lobby.parseMessage("test", playerOne, playerTwo));
        verify(socketOne, never()).sendEvent(any(), any());
    }

    @Test
    public void testParseMessageNoMatch() {
        assertTrue(lobby.parseMessage("!test", playerOne, playerTwo));
        verify(socketOne, times(1)).sendEvent(eq("errorEvent"), any());
    }

    @Test
    public void testParseMessageWin() {
        playerOne.state = UserState.YourTurn;
        assertTrue(lobby.parseMessage("!win", playerOne, playerTwo));
        verify(playerOne, times(1)).setState(UserState.GameWon);
        verify(playerTwo, times(1)).setState(UserState.GameLost);
    }

    @Test
    public void testParseMessagePlayMinecraft() {
        assertTrue(lobby.parseMessage("!play minecraft cat", playerOne, playerTwo));
        verify(socketOne, times(1)).sendEvent(eq("command"), commandArgumentCaptor.capture());

        assertEquals("play", commandArgumentCaptor.getValue().commandName);
        assertNull(commandArgumentCaptor.getValue().sender);
        assertEquals("minecraft", commandArgumentCaptor.getValue().params[0]);
        assertEquals("cat", commandArgumentCaptor.getValue().params[1]);
    }

    @Test
    public void testParseMessagePlayNotMinecraft() {
        assertTrue(lobby.parseMessage("!play notminecraft cat", playerOne, playerTwo));
        verify(socketOne, never()).sendEvent(eq("command"), any());
    }

    @Test
    public void testParseMessageStop() {
        assertTrue(lobby.parseMessage("!stop", playerOne, playerTwo));
        verify(socketOne, times(1)).sendEvent(eq("command"), commandArgumentCaptor.capture());

        assertEquals("stop", commandArgumentCaptor.getValue().commandName);
        assertNull(commandArgumentCaptor.getValue().sender);
    }

    @Test
    public void testParseMessageVolume() {
        assertTrue(lobby.parseMessage("!volume 1", playerOne, playerTwo));
        verify(socketOne, times(1)).sendEvent(eq("command"), commandArgumentCaptor.capture());

        assertEquals("volume", commandArgumentCaptor.getValue().commandName);
        assertNull(commandArgumentCaptor.getValue().sender);
        assertEquals("1", commandArgumentCaptor.getValue().params[0]);
    }

    @Test
    public void testParseMessageVolumeInvalidAmountOfParameters() {
        assertTrue(lobby.parseMessage("!volume 5 5", playerOne, playerTwo));
        verify(socketOne, never()).sendEvent(eq("command"), any());
    }

    @Test
    public void testParseMessageVolumeInvalidAmount() {
        assertTrue(lobby.parseMessage("!volume 2", playerOne, playerTwo));
        verify(socketOne, never()).sendEvent(eq("command"), any());
    }

    @Test
    public void testParseMessageYoutubeStop() {
        assertTrue(lobby.parseMessage("!youtube stop", playerOne, playerTwo));
        verify(socketOne, times(1)).sendEvent(eq("command"), commandArgumentCaptor.capture());

        assertEquals("youtube", commandArgumentCaptor.getValue().commandName);
        assertNull(commandArgumentCaptor.getValue().sender);
        assertEquals("stop", commandArgumentCaptor.getValue().params[0]);
    }

    @Test
    public void testParseMessageYoutubePlay() {
        assertTrue(lobby.parseMessage("!youtube https://www.youtube.com/watch?v=b67TUmzmos0", playerOne, playerTwo));
        verify(socketOne, times(1)).sendEvent(eq("command"), commandArgumentCaptor.capture());

        assertEquals("youtube", commandArgumentCaptor.getValue().commandName);
        assertNull(commandArgumentCaptor.getValue().sender);
        assertEquals("https://youtube.com/embed/b67TUmzmos0?autoplay=1&showinfo=0&controls=0", commandArgumentCaptor.getValue().params[0]);
    }

    @Test
    public void testParseMessageYoutubePlayInvalidLink() {
        assertTrue(lobby.parseMessage("!youtube play https://google.nl", playerOne, playerTwo));
        verify(socketOne, never()).sendEvent(eq("command"), commandArgumentCaptor.capture());

//        assertEquals("youtube", commandArgumentCaptor.getValue().commandName);
//        assertNull(commandArgumentCaptor.getValue().sender);
//        assertEquals("stop", commandArgumentCaptor.getValue().params[0]);
    }

    @Test
    public void testParseMessageYoutubeInvalidAmountOfParameters() {
        assertTrue(lobby.parseMessage("!youtube stop stop", playerOne, playerTwo));
        verify(socketOne, never()).sendEvent(eq("command"), any());
    }
}

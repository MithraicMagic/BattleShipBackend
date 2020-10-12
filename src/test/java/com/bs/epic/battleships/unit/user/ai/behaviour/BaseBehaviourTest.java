package com.bs.epic.battleships.unit.user.ai.behaviour;

import com.bs.epic.battleships.game.Game;
import com.bs.epic.battleships.lobby.Lobby;
import com.bs.epic.battleships.rest.repository.dto.AiMessage;
import com.bs.epic.battleships.user.ai.behaviour.easy.EasyBehaviour;
import com.bs.epic.battleships.user.player.Player;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.*;

public class BaseBehaviourTest {
    Lobby lobby = mock(Lobby.class);
    Player player = mock(Player.class);

    @Test
    public void testOnSetup() {
        lobby.game = mock(Game.class);
        player.uid = "test";

        EasyBehaviour easyBehaviour = new EasyBehaviour(0, List.of());
        easyBehaviour.onSetup(lobby, player);

        verify(lobby.game, after(100).times(1)).autoPlaceShips(player);
        verify(lobby, after(100).times(1)).donePlacing("test");
    }

    @Test
    public void testOnGameLost() {
        EasyBehaviour easyBehaviour = new EasyBehaviour(0, List.of());
        easyBehaviour.onGameLost(lobby, player);

        verify(lobby, times(1)).sendMessage("Well played.", player);
        verify(lobby, times(1)).onRematchRequest(player);
    }

    @Test
    public void testOnGameWon() {
        EasyBehaviour easyBehaviour = new EasyBehaviour(0, List.of());
        easyBehaviour.onGameWon(lobby, player);

        verify(lobby, times(1)).sendMessage("OMG EASY GAME.", player);
        verify(lobby, times(1)).onRematchRequest(player);
    }

    @Test
    public void testOnMessageReceived() {
        EasyBehaviour easyBehaviour = new EasyBehaviour(0, List.of(new AiMessage("Wow, cool!")));
        easyBehaviour.onMessageReceived(lobby, player);

        verify(lobby, times(1)).sendMessage("Wow, cool!", player);
    }
}

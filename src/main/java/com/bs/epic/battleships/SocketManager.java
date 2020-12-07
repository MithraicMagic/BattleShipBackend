package com.bs.epic.battleships;

import com.bs.epic.battleships.documentation.Documentation;
import com.bs.epic.battleships.events.*;
import com.bs.epic.battleships.game.grid.GridPos;
import com.bs.epic.battleships.lobby.Lobby;
import com.bs.epic.battleships.lobby.LobbyManager;
import com.bs.epic.battleships.rest.service.AuthService;
import com.bs.epic.battleships.rest.service.MessageService;
import com.bs.epic.battleships.user.*;
import com.bs.epic.battleships.user.ai.AIPlayer;
import com.bs.epic.battleships.user.player.Player;
import com.bs.epic.battleships.util.result.ShootSuccess;
import com.bs.epic.battleships.util.Util;
import com.bs.epic.battleships.verification.AuthValidator;
import com.bs.epic.battleships.verification.JwtUtil;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class SocketManager {
    SocketIOServer server;
    Configuration config;
    
    private final LobbyManager lobbyManager;
    private final UserManager userManager;
    private final SocketEvents socketEvents;

    public SocketManager(JwtUtil jwtUtil, AuthService authService, AuthValidator authValidator, MessageService messageService) {
        lobbyManager = new LobbyManager();
        userManager = new UserManager();
        config = new Configuration();

        this.socketEvents = new SocketEvents(jwtUtil, userManager, lobbyManager, authValidator, authService, messageService);
        this.init();
    }

    public void init() {
        config.setHostname("0.0.0.0");
        config.setPort(6003);
        config.setContext("/sockets");

        server = new SocketIOServer(config);

        var documentation = Documentation.get();
        documentation.setSocketServer(server);

        server.addDisconnectListener((socket) -> {
            var user = userManager.getBySocket(socket);
            if (user == null) return;

            if (user.type == UserType.User) {
                userManager.remove(user);
                return;
            }

            var player = (Player) user;
            player.setReconnecting();

            var lobby = lobbyManager.getLobbyBySocket(socket);
            if (lobby == null) return;

            lobby.onPlayerDisconnect(player);
        });

        documentation.addEventListener("lastUid", Uid.class, Reconnect.class, socketEvents::onLastUid);
        documentation.addEventListener("inputUsername", Name.class, NameAccepted.class, socketEvents::onInputUsername);
        documentation.addEventListener("getLobbyInfo", Code.class, Name.class, socketEvents::onGetLobbyInfo);
        documentation.addEventListener("tryCode", Code.class, LobbyJoined.class, socketEvents::onTryCode);
        documentation.addEventListener("singlePlayerSettings", Uid.class, State.class,
                socketEvents::onSinglePlayerSettings);
        documentation.addEventListener("startSinglePlayerLobby", StartSinglePlayerLobby.class, LobbyJoined.class,
                socketEvents::onStartSinglePlayerLobby);
        documentation.addEventListener("leaveLobby", LeaveLobby.class, null, socketEvents::onLeaveLobby);
        documentation.addEventListener("startSetup", LobbyId.class, null, socketEvents::onStartup);
        documentation.addEventListener("placeShip", PlaceShip.class, null, socketEvents::onPlaceShip);
        documentation.addEventListener("autoPlaceShips", AutoPlaceShips.class, Ships.class,
                socketEvents::onAutoPlaceShips);
        documentation.addEventListener("removeShip", RemoveShip.class, null, socketEvents::onRemoveShip);
        documentation.addEventListener("submitSetup", DonePlacing.class, null, socketEvents::onSubmitSetup);
        documentation.addEventListener("shoot", Shoot.class, ShootResult.class, socketEvents::onShoot);
        documentation.addEventListener("getSetupData", Uid.class, SetupData.class, socketEvents::onGetSetupData);
        documentation.addEventListener("getNameData", Uid.class, NameData.class, socketEvents::onGetNameData);
        documentation.addEventListener("getGameData", Uid.class, GameData.class, socketEvents::onGetGameData);
        documentation.addEventListener("sendMessage", Message.class, MessageReceived.class,
                socketEvents::onSendMessage);
        documentation.addEventListener("getMessages", Uid.class, Messages.class, socketEvents::onGetMessages);
        documentation.addEventListener("rematch", Rematch.class, null, socketEvents::onRematch);
        documentation.addEventListener("loggedInUserWon", LoggedInUserWon.class, null, socketEvents::onLoggedInUserWon);

        server.start();
    }
}

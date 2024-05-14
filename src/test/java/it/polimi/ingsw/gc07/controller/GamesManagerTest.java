package it.polimi.ingsw.gc07.controller;

import it.polimi.ingsw.gc07.enumerations.CommandResult;
import it.polimi.ingsw.gc07.enumerations.TokenColor;
import it.polimi.ingsw.gc07.network.VirtualServerGamesManager;
import it.polimi.ingsw.gc07.network.rmi.RmiClient;
import it.polimi.ingsw.gc07.network.rmi.RmiServerGamesManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import static org.junit.jupiter.api.Assertions.*;

class GamesManagerTest {
    GamesManager gm;

    @BeforeEach
    void setUp() {
        gm = GamesManager.getGamesManager();
        gm.resetGamesManager();
    }

    @Test
    void singletonTest() {
        GamesManager gm1 = GamesManager.getGamesManager();
        GamesManager gm2 = GamesManager.getGamesManager();
        assertSame(gm1, gm2);
    }

    @Test
    void testJoinNewGame() throws RemoteException, NotBoundException {
        String name = "VirtualServerGamesManager";
        RmiServerGamesManager serverGamesManager = RmiServerGamesManager.getRmiServerGamesManager();
        Registry registry = LocateRegistry.createRegistry(1234);
        registry.rebind(name, serverGamesManager);

        Registry registry2 = LocateRegistry.getRegistry("127.0.0.1", 1234);
        VirtualServerGamesManager rmiServerGamesManager = (VirtualServerGamesManager) registry2.lookup("VirtualServerGamesManager");
        RmiClient newRmiClient = new RmiClient("Player1", true, rmiServerGamesManager);
        newRmiClient.connectToGamesManagerServer(true, true);
        gm.addPlayerToPending("player1", true, true);
        VirtualServerGamesManager rmiServerGamesManager2 = (VirtualServerGamesManager) registry2.lookup("VirtualServerGamesManager");
        RmiClient newRmiClient2 = new RmiClient("Player1", true, rmiServerGamesManager2);
        newRmiClient2.connectToGamesManagerServer(true, true);
        gm.addPlayerToPending("player2", true, true);
        assertNull(gm.getGameById(0));

        gm.joinNewGame("player1", TokenColor.GREEN, 2);
        assertEquals(CommandResult.SUCCESS, gm.getCommandResult());

        gm.joinExistingGame("player2", TokenColor.RED, 0);
        assertEquals(CommandResult.SUCCESS, gm.getCommandResult());

        assertNotNull(gm.getGameById(0));


        assertEquals(0, gm.getGameIdWithPlayer("player1"));
    }

    @Test
    void checkReconnection() throws RemoteException, NotBoundException {
        String name = "VirtualServerGamesManager";
        RmiServerGamesManager serverGamesManager = RmiServerGamesManager.getRmiServerGamesManager();
        Registry registry = LocateRegistry.createRegistry(1234);
        registry.rebind(name, serverGamesManager);

        Registry registry2 = LocateRegistry.getRegistry("127.0.0.1", 1234);
        VirtualServerGamesManager rmiServerGamesManager = (VirtualServerGamesManager) registry2.lookup("VirtualServerGamesManager");
        RmiClient newRmiClient = new RmiClient("Player1", true, rmiServerGamesManager);
        newRmiClient.connectToGamesManagerServer(true, true);
        gm.addPlayerToPending("player1", false, true);

        VirtualServerGamesManager rmiServerGamesManager2 = (VirtualServerGamesManager) registry2.lookup("VirtualServerGamesManager");
        RmiClient newRmiClient2 = new RmiClient("Player1", true, rmiServerGamesManager2);
        newRmiClient2.connectToGamesManagerServer(true, true);
        gm.addPlayerToPending("player2", false, true);

        gm.joinNewGame("player1", TokenColor.GREEN, 2);
        gm.joinExistingGame("player2", TokenColor.RED, 0);

        GameController gc = GamesManager.getGamesManager().getGameById(0);
        assertEquals(GameState.PLACING_STARTER_CARDS, gc.getState());

        gc.setState(GameState.PLAYING);

        gc.disconnectPlayer("player1");
        assertEquals(GameState.WAITING_RECONNECTION, gc.getState());

        gm.addPlayerToPending("player1", true, true);
        assertEquals(GameState.PLAYING, gc.getState());
    }
}
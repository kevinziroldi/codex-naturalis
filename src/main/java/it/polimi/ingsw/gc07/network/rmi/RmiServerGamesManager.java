package it.polimi.ingsw.gc07.network.rmi;

import it.polimi.ingsw.gc07.enumerations.NicknameCheck;
import it.polimi.ingsw.gc07.controller.GamesManager;
import it.polimi.ingsw.gc07.game_commands.GamesManagerCommand;
import it.polimi.ingsw.gc07.network.VirtualServerGame;
import it.polimi.ingsw.gc07.network.VirtualServerGamesManager;
import it.polimi.ingsw.gc07.network.VirtualView;
import it.polimi.ingsw.gc07.updates.ExistingGamesUpdate;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class RmiServerGamesManager extends UnicastRemoteObject implements VirtualServerGamesManager {
    /**
     * Instance of RmiServerGamesManager.
     */
    private static RmiServerGamesManager myRmiServerGamesManager;
    /**
     * Map containing the RmiServerGame of every game.
     */
    private final Map<Integer, RmiServerGame> rmiServerGames;
    /**
     * Queue containing commands to execute.
     */
    private final BlockingDeque<GamesManagerCommand> commandsQueue;

    /**
     * Constructor of class RmiServerGamesManager.
     */
    private RmiServerGamesManager() throws RemoteException {
        this.rmiServerGames = new HashMap<>();
        this.commandsQueue = new LinkedBlockingDeque<>();
        startCommandExecutor();
    }

    /**
     * Method used to get the only existing instance of RmiServerGamesManager,
     * which uses the Singleton pattern.
     * @return RmiServerGamesManager instance
     */
    public static synchronized RmiServerGamesManager getRmiServerGamesManager() {
        if(myRmiServerGamesManager == null) {
            try {
                myRmiServerGamesManager = new RmiServerGamesManager();
            }catch(RemoteException e) {
                // TODO
                e.printStackTrace();
                throw new RuntimeException();
            }
        }
        return myRmiServerGamesManager;
    }

    /**
     * Method that starts the command executor, a thread that takes tasks
     * from the blocking queue and executes them.
     * Used to make RMI asynchronous.
     */
    private void startCommandExecutor() {
        new Thread(() -> {
            while(true) {
                try {
                    GamesManagerCommand command = commandsQueue.take();
                    GamesManager.getGamesManager().setAndExecuteCommand(command);

                    // only for testing
                    System.out.println(GamesManager.getGamesManager().getCommandResult());
                }catch(InterruptedException e) {
                    System.err.println("Channel closed");
                    break;
                }
            }
        }).start();
    }

    /**
     * Method that allows a client to connect with the RMI server.
     * @param client client to connect
     * @throws RemoteException remote exception
     */
    @Override
    public synchronized void connect(String nickname, VirtualView client) throws RemoteException {
        GamesManager.getGamesManager().addVirtualView(nickname, client);
        System.err.println("New client connected");
    }

    @Override
    public NicknameCheck checkNickname(String nickname) throws RemoteException {
        return GamesManager.getGamesManager().checkNickname(nickname);
    }

    @Override
    public VirtualServerGame getGameServer(int gameId) throws RemoteException {
        assert(rmiServerGames.containsKey(gameId));
        return rmiServerGames.get(gameId);
    }

    /**
     * Method that allows the client to execute a games manager command.
     * @param gamesManagerCommand games manager command to set and execute
     * @throws RemoteException remote exception
     */
    @Override
    public synchronized void setAndExecuteCommand(GamesManagerCommand gamesManagerCommand) throws RemoteException {
        // add command to queue
        try {
            // blocking queues are thread safe
            commandsQueue.put(gamesManagerCommand);
        }catch(InterruptedException e) {
            // TODO
            e.printStackTrace();
            throw new RemoteException();
        }
    }

    /**
     * Method that allows to create a new RmiServerGame, used when the player joins a new game.
     * @param gameId game id
     */
    public void createServerGame(int gameId) {
        assert(!rmiServerGames.containsKey(gameId));
        try {
            rmiServerGames.put(gameId, new RmiServerGame(GamesManager.getGamesManager().getGameById(gameId)));
        }catch(RemoteException e) {
            // TODO
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    /**
     * Method that allows to set a RmiServerGame for the client.
     * @param nickname player's nickname
     * @param gameId game id
     */
    public void setServerGame(String nickname, int gameId) {
        assert(rmiServerGames.containsKey(gameId));
        try {
            VirtualView virtualView = GamesManager.getGamesManager().getVirtualView(nickname);
            if(virtualView == null) {
                throw new RuntimeException();
            }
            virtualView.setServerGame(gameId);
        }catch(RemoteException e) {
            // TODO
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    /**
     * Method used to notify that joining was not successful.
     */
    public void notifyJoinNotSuccessful(String nickname) {
        try {
            VirtualView virtualView = GamesManager.getGamesManager().getVirtualView(nickname);
            if(virtualView == null) {
                throw new RuntimeException();
            }
            virtualView.notifyJoinNotSuccessful();
        }catch(RemoteException e) {
            // TODO
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    /**
     * Method used to display existing games to a player who requests it.
     * @param nickname player's nickname
     */
    public void displayGames(String nickname) {
        try {
            // get virtual view
            VirtualView virtualView = GamesManager.getGamesManager().getVirtualView(nickname);
            if(virtualView == null) {
                throw new RuntimeException();
            }
            ExistingGamesUpdate update = new ExistingGamesUpdate(GamesManager.getGamesManager().getFreeGamesDetails());
            virtualView.receiveExistingGamesUpdate(update);
        }catch(RemoteException e) {
            // TODO
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    /**
     * Method used to delete the game server from the map once the game is ended.
     * @param gameId game id of the game to remove
     */
    public void deleteGame(int gameId) {
        // delete server game
        RmiServerGame rmiServerGame = null;
        for(int id: rmiServerGames.keySet()) {
            if(id == gameId) {
                rmiServerGame = rmiServerGames.get(id);
            }
        }
        if(rmiServerGame != null){
            rmiServerGames.remove(gameId);
        }
    }
}

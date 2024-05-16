package it.polimi.ingsw.gc07.network.rmi;

import it.polimi.ingsw.gc07.controller.GamesManager;
import it.polimi.ingsw.gc07.game_commands.*;
import it.polimi.ingsw.gc07.model_view.GameView;
import it.polimi.ingsw.gc07.network.*;
import it.polimi.ingsw.gc07.updates.*;
import it.polimi.ingsw.gc07.view.Ui;
import it.polimi.ingsw.gc07.view.gui.Gui;
import it.polimi.ingsw.gc07.view.tui.Tui;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class RmiClient extends UnicastRemoteObject implements Client, VirtualView, PingSender {
    /**
     * Nickname of the player associated to the RmiClient.
     */
    private final String nickname;
    /**
     * Reference to RmiServerGamesManager, the general server.
     */
    private final VirtualServerGamesManager serverGamesManager;
    /**
     * Reference to RmiServerGame, the game specific server.
     */
    private VirtualServerGame serverGame;
    /**
     * Player's local copy of the game model.
     */
    private final GameView gameView;
    /**
     * Send ping attribute.
     */
    private boolean clientAlive;
    /**
     * Player's ui.
     */
    private final Ui ui;

    /**
     * Boolean that is true if the server is on
     */
    private boolean pong;
    /**
     * Blocking queue containing received updates.
     */
    private final BlockingDeque<Update> updatesQueue;
    /**
     * Number of missed pongs to detect a disconnection.
     */
    private static final int maxMissedPongs = 3;

    /**
     * Constructor of RmiClient.
     * @param serverGamesManager general server
     * @param nickname nickname
     * @throws RemoteException remote exception
     */
    public RmiClient(String nickname, boolean interfaceType, VirtualServerGamesManager serverGamesManager) throws RemoteException {
        this.nickname = nickname;
        this.serverGamesManager = serverGamesManager;
        this.serverGame = null;
        this.gameView = new GameView(nickname);
        this.clientAlive = true;
        if(interfaceType) {
            this.ui = new Gui();
        }else {
            this.ui = new Tui(nickname, this);
        }
        this.gameView.addViewListener(ui);
        this.pong = true;
        this.updatesQueue = new LinkedBlockingDeque<>();
        startUpdateExecutor();
    }

    /**
     * Getter method for nickname
     * @return nickname
     * @throws RemoteException remote exception
     */
    @Override
    public String getNickname() throws RemoteException {
        // TODO da cancellare quando tomasso rimuove da socket
        return nickname;
    }

    @Override
    public synchronized boolean isClientAlive() {
        return clientAlive;
    }

    @Override
    public synchronized void setClientAlive(boolean isAlive) {
        this.clientAlive = isAlive;
    }

    @Override
    public GameView getGameView() {
        return gameView;
    }

    @Override
    public void setAndExecuteCommand(GamesManagerCommand gamesManagerCommand) {
        try {
            serverGamesManager.setAndExecuteCommand(gamesManagerCommand);
        }catch(RemoteException e) {
            // if not already detected by ping
            System.out.println("Connection failed. Press enter. - set and execute games manager");
            clientAlive = false;
        }
    }

    @Override
    public void setAndExecuteCommand(GameControllerCommand gameCommand) {
        try {
            serverGame.setAndExecuteCommand(gameCommand);
        }catch(RemoteException e) {
            // if not already detected by ping
            System.out.println("Connection failed. Press enter. - set and execute game");
            clientAlive = false;
        }
    }

    /**
     * Method that allows the client to connect with RMIServerGamesManager, the general server.
     * @param connectionType connection type
     * @param interfaceType interface type
     */
    public void connectToGamesManagerServer(boolean connectionType, boolean interfaceType) {
        try {
            serverGamesManager.setAndExecuteCommand(new AddPlayerToPendingCommand(nickname, connectionType, interfaceType));
            serverGamesManager.connect(nickname, this);
        } catch (RemoteException e) {
            // TODO
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void reconnectPlayer(String nickname, boolean connectionType, boolean interfaceType) {
        try {
            serverGamesManager.setAndExecuteCommand(new ReconnectPlayerCommand(this, nickname, connectionType, interfaceType));
        } catch (RemoteException e) {
            // TODO
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Method that allows to set a RmiServerGame, the game specific server, for the client.
     * @param gameId game id
     * @throws RemoteException remote exception
     */
    @Override
    public void setServerGame(int gameId) throws RemoteException {
        try {
            this.serverGame = serverGamesManager.getGameServer(gameId);
        }catch(RemoteException e) {
            // TODO
            e.printStackTrace();
            throw new RuntimeException();
        }
        // game joined
        new Thread(this::startGamePing).start();
        new Thread(this::checkPong).start();
        new Thread(this::runCliGame).start();
    }

    /**
     * Method used from RmiServerGamesManager to restart the cli if the joining was not successful.
     */
    public void notifyJoinNotSuccessful() throws RemoteException {
        new Thread(this::runCliJoinGame).start();
    }

    @Override
    public void sendPong() throws RemoteException {
        setPong();
    }

    private synchronized void setPong() {
        this.pong = true;
    }

    /**
     * Method that checks if the client is receiving pongs from server.
     */
    private void checkPong() {
        int missedPong = 0;
        while(true){
            synchronized(this) {
                if(pong) {
                    missedPong = 0;
                }else {
                    missedPong ++;
                    if(missedPong >= maxMissedPongs) {
                        System.out.println("you lost the connection :(");
                        clientAlive = false;
                        break;
                    }
                }
                pong = false;
            }
            try {
                Thread.sleep(1000); // wait one second between two pong checks
            } catch (InterruptedException e) {
                // TODO
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }
    @Override
    public void startGamePing() {
        while(true) {
            synchronized (this) {
                if (clientAlive) {
                    try {
                        serverGame.setAndExecuteCommand(new SendPingCommand(nickname));
                    }catch(RemoteException e) {
                        // connection failed
                        System.out.println("Connection failed. Press enter. - ping");
                        clientAlive = false;
                    }
                } else{
                    break;
                }
            }
            try {
                Thread.sleep(1000); // wait one second between two ping
            } catch (InterruptedException e) {
                // TODO
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

    public void runCliJoinGame() {
        assert(ui != null);
        ui.runCliJoinGame();
    }

    public void runCliGame() {
        assert(ui != null);
        ui.runCliGame();
    }

    private void receiveUpdate(Update update) {
        try {
            // blocking queues are thread safe
            updatesQueue.put(update);
        }catch(InterruptedException e) {
            // TODO
            e.printStackTrace();
            throw new RuntimeException();
        }
        setPong();
    }

    private void startUpdateExecutor() {
        new Thread(() -> {
            while(clientAlive) {
                try {
                    Update update = updatesQueue.take();
                    synchronized (this) {
                        update.execute(gameView);
                    }
                }catch(InterruptedException e) {
                    // TODO
                    e.printStackTrace();
                    throw new RuntimeException();
                }
            }
        }).start();
    }

    /**
     * Method used to notify the player he has received a new chat chatMessage.
     * @param chatMessageUpdate chat message update
     */
    @Override
    public void receiveChatMessageUpdate(ChatMessageUpdate chatMessageUpdate) {
        receiveUpdate(chatMessageUpdate);
    }

    /**
     * Method used to show the client his starter card.
     * @param starterCardUpdate starter card update
     */
    @Override
    public void receiveStarterCardUpdate(StarterCardUpdate starterCardUpdate) {
        receiveUpdate(starterCardUpdate);
    }

    /**
     * Method used to notify players that a card has been placed.
     * @param placedCardUpdate placed card update
     */
    @Override
    public void receivePlacedCardUpdate(PlacedCardUpdate placedCardUpdate) {
        receiveUpdate(placedCardUpdate);
    }

    /**
     * Method used to notify a game model update.
     * @param gameModelUpdate game model update
     */
    @Override
    public void receiveGameModelUpdate(GameModelUpdate gameModelUpdate) {
        receiveUpdate(gameModelUpdate);
    }

    /**
     * Method used to notify a player joined update.
     * @param playerJoinedUpdate player joined update
     * @throws RemoteException remote exception
     */
    @Override
    public void receivePlayerJoinedUpdate(PlayerJoinedUpdate playerJoinedUpdate) throws RemoteException {
        receiveUpdate(playerJoinedUpdate);
    }

    /**
     * Method used to notify a command result update.
     * @param commandResultUpdate command result update
     */
    @Override
    public void receiveCommandResultUpdate(CommandResultUpdate commandResultUpdate) {
        receiveUpdate(commandResultUpdate);
    }

    /**
     * Method used to send a stall update.
     * @param stallUpdate stall update
     */
    @Override
    public void receiveStallUpdate(StallUpdate stallUpdate) {
        receiveUpdate(stallUpdate);
    }

    /**
     * Method used to send a connection update.
     * @param connectionUpdate connection update
     */
    @Override
    public void receiveConnectionUpdate(ConnectionUpdate connectionUpdate) {
        receiveUpdate(connectionUpdate);
    }

    /**
     * Method used to send a card hand update.
     * @param cardHandUpdate card hand update
     */
    @Override
    public void receiveCardHandUpdate(CardHandUpdate cardHandUpdate) {
        receiveUpdate(cardHandUpdate);
    }

    /**
     * Method used to show the client an updated score.
     * @param scoreUpdate score update
     */
    @Override
    public void receiveScoreUpdate(ScoreUpdate scoreUpdate) {
        receiveUpdate(scoreUpdate);
    }

    /**
     * Method used to show the client existing games.
     * @param existingGamesUpdate existing games update
     */
    @Override
    public synchronized void receiveExistingGamesUpdate(ExistingGamesUpdate existingGamesUpdate) {
        existingGamesUpdate.execute(gameView);
        ui.runCliJoinGame();
    }

    /**
     * Method used to show the client a deck update.
     * @param deckUpdate deck update
     */
    @Override
    public void receiveDeckUpdate(DeckUpdate deckUpdate) throws RemoteException {
        receiveUpdate(deckUpdate);
    }

    @Override
    public void receiveGameEndedUpdate(GameEndedUpdate gameEndedUpdate) throws RemoteException {
        receiveUpdate(gameEndedUpdate);
    }
}

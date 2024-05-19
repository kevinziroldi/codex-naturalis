package it.polimi.ingsw.gc07.controller;

import it.polimi.ingsw.gc07.updates.ExistingGamesUpdate;
import it.polimi.ingsw.gc07.utils.DecksBuilder;
import it.polimi.ingsw.gc07.enumerations.NicknameCheck;
import it.polimi.ingsw.gc07.game_commands.GamesManagerCommand;
import it.polimi.ingsw.gc07.model.cards.DrawableCard;
import it.polimi.ingsw.gc07.model.cards.GoldCard;
import it.polimi.ingsw.gc07.model.decks.DrawableDeck;
import it.polimi.ingsw.gc07.enumerations.CommandResult;
import it.polimi.ingsw.gc07.exceptions.WrongNumberOfPlayersException;
import it.polimi.ingsw.gc07.model.Player;
import it.polimi.ingsw.gc07.model.cards.ObjectiveCard;
import it.polimi.ingsw.gc07.model.cards.PlaceableCard;
import it.polimi.ingsw.gc07.model.decks.Deck;
import it.polimi.ingsw.gc07.model.decks.PlayingDeck;
import it.polimi.ingsw.gc07.enumerations.TokenColor;
import it.polimi.ingsw.gc07.network.VirtualView;
import it.polimi.ingsw.gc07.network.rmi.RmiServerGamesManager;
import it.polimi.ingsw.gc07.network.socket.SocketServer;

import java.rmi.RemoteException;
import java.util.*;

public class GamesManager {
    /**
     * Instance of GamesManager.
     */
    private static GamesManager myGamesManager;
    /**
     * List of game controllers.
     */
    private List<GameController> gameControllers;
    /**
     * List of players who have not chosen a game.
     */
    private List<Player> pendingPlayers;
    /**
     * Command result manager for games manager.
     */
    private CommandResult commandResult;
    /**
     * Map containing pending players' virtual views.
     */
    private Map<String, VirtualView> playerVirtualViews;
    /**
     * Map containing players' timers.
     */
    private Map<String, Timer> playersTimers;

    /**
     * GamesManger is created once the server is started.
     * GamesManager implements Singleton pattern.
     */
    private GamesManager() {
        this.gameControllers = new ArrayList<>();
        this.pendingPlayers = new ArrayList<>();
        this.commandResult = null;
        this.playerVirtualViews = new HashMap<>();
        this.playersTimers = new HashMap<>();
    }

    /**
     * Method to get the only available instance of GamesManager (Singleton pattern).
     * @return instance of games manager
     */
    public static synchronized GamesManager getGamesManager() {
        if(myGamesManager == null) {
            myGamesManager = new GamesManager();
        }
        return myGamesManager;
    }

    public synchronized void addVirtualView(String nickname, VirtualView virtualView) {
        assert(!playerVirtualViews.containsKey(nickname));
        playerVirtualViews.put(nickname, virtualView);
    }

    public synchronized VirtualView getVirtualView(String nickname) {
        assert(playerVirtualViews.containsKey(nickname));
        return playerVirtualViews.get(nickname);
    }

    /**
     * Only for test purposes, return a new instance of GamesManager, not using Singleton.
     */
    // used in tests
    void resetGamesManager() {
        gameControllers = new ArrayList<>();
        pendingPlayers = new ArrayList<>();
        commandResult = null;
        playerVirtualViews = new HashMap<>();
        playersTimers = new HashMap<>();
    }

    /**
     * Return the game with the provided id.
     * @return game with given id
     */
    public synchronized GameController getGameById(int id) {
        for(GameController g: gameControllers) {
            if(g.getId() == id) {
                return g;
            }
        }
        return null;
    }

    /**
     * Getter for the command result manager.
     * @return command result manager of the games manager
     */
    // used for tests
    CommandResult getCommandResult() {
        return commandResult;
    }

    /**
     * Setter for gameControllers manager command (command pattern).
     * @param gamesManagerCommand games manager command
     */
    public synchronized void setAndExecuteCommand(GamesManagerCommand gamesManagerCommand) {
        gamesManagerCommand.execute(this);
    }

    /**
     * Method that returns the object corresponding to the pending player with a certain name.
     * @param nickname nickname of the pending player to search.
     * @return pending player with the given nicknames
     */
    Player getPendingPlayer(String nickname) {
        Player player = null;
        for(Player p: pendingPlayers) {
            if(p.getNickname().equals(nickname)){
                player = p;
            }
        }
        return player;
    }

    /**
     * Finds the game id of the game in which is playing the player with provided nickname.
     * @param nickname nickname of the player
     * @return game id
     */
    // used for tests
    int getGameIdWithPlayer(String nickname) {
        for(GameController g: gameControllers) {
            if(g.hasPlayer(nickname)) {
                return g.getId();
            }
        }
        return -1;
    }

    public void addPlayerToPending(String nickname, boolean connectionType, boolean interfaceType) {
        // this command can always be used
        assert(!checkReconnection(nickname));
        assert(checkNicknameUnique(nickname));

        Player newPlayer = new Player(nickname, connectionType, interfaceType);
        pendingPlayers.add(newPlayer);
        new Thread(()->{
            Timer timeout = new Timer();
            playersTimers.put(nickname, timeout);
            timeout.schedule(new TimerTask() {
                @Override
                public void run() {
                    synchronized (this){
                        removePlayer(nickname);
                    }
                }
            }, 60 * 1000); //timer of 1 minute
        }).start();
        commandResult = CommandResult.SUCCESS;
    }

    private boolean checkReconnection(String nickname) {
        boolean reconnection = false;
        for(GameController gameController: gameControllers) {
            // if a game has a player with the same nickname and disconnected
            for(Player p: gameController.getPlayers()) {
                if(p.getNickname().equals(nickname) && !p.isConnected()) {
                    reconnection = true;
                    break;
                }
            }
        }
        return reconnection;
    }

    public void reconnectPlayer(VirtualView client, String nickname, boolean connectionType, boolean interfaceType) {
        for(GameController g: gameControllers) {
            if(g.hasPlayer(nickname)) {
                g.reconnectPlayer(client, nickname, connectionType, interfaceType);
            }
        }
    }

    /**
     * Method to check if a nickname is unique or another player has the same nickname.
     * @param nickname nickname to check
     * @return true if no other player has the same nickname
     */
    private boolean checkNicknameUnique(String nickname) {
        boolean unique = true;
        for(GameController g: gameControllers) {
            if(g.hasPlayer(nickname)) {
                unique = false;
            }
        }
        for(Player p: pendingPlayers) {
            if(p.getNickname().equals(nickname)) {
                unique = false;
                break;
            }
        }
        return unique;
    }

    public synchronized NicknameCheck checkNickname(String nickname) {
        if(checkReconnection(nickname)) {
            return NicknameCheck.RECONNECTION;
        }else if(!checkNicknameUnique(nickname)) {
            return NicknameCheck.EXISTING_NICKNAME;
        }else {
            return NicknameCheck.NEW_NICKNAME;
        }
    }

    public void joinExistingGame(String nickname, TokenColor tokenColor, int gameId) {
        // this command can always be used
        Player player = getPendingPlayer(nickname);
        assert(player != null);
        boolean found = false;
        for(GameController gameController : gameControllers) {
            if(gameController.getId() == gameId) {
                found = true;
                // check gameController state WAITING_PLAYERS
                if(!gameController.getState().equals(GameState.GAME_STARTING)) {
                    commandResult = CommandResult.GAME_FULL;
                    VirtualView virtualView = getVirtualView(nickname);
                    if(virtualView == null) { //TODO come sotto per la mappa
                        throw new RuntimeException();
                    }
                    try {
                        virtualView.notifyJoinNotSuccessful();
                    } catch (RemoteException ex) {
                        //TODO
                        throw new RuntimeException(ex);
                    }
                    return;
                }
                // check token color unique
                if(gameController.hasPlayerWithTokenColor(tokenColor)) {
                    commandResult = CommandResult.TOKEN_COLOR_ALREADY_TAKEN;
                    VirtualView virtualView = getVirtualView(nickname);
                    if(virtualView == null) { //TODO come sotto per la mappa
                        throw new RuntimeException();
                    }
                    try {
                        virtualView.notifyJoinNotSuccessful();
                    } catch (RemoteException ex) {
                        //TODO
                        throw new RuntimeException(ex);
                    }
                    return;
                }
                VirtualView virtualView = getVirtualView(nickname);
                if(virtualView == null) { //TODO come sopra per la mappa
                    throw new RuntimeException();
                }
                try {
                    virtualView.setServerGame(gameId);
                } catch (RemoteException ex) {
                    //TODO
                    throw new RuntimeException(ex);
                }
                player.setTokenColor(tokenColor);
                gameController.addPlayer(player, playerVirtualViews.get(nickname));
            }
        }
        if(!found){
            commandResult = CommandResult.GAME_NOT_PRESENT;
            VirtualView virtualView = getVirtualView(nickname);
            if(virtualView == null) { //TODO come sotto per la mappa
                throw new RuntimeException();
            }
            try {
                virtualView.notifyJoinNotSuccessful();
            } catch (RemoteException ex) {
                //TODO
                throw new RuntimeException(ex);
            }
            return;
        }
        // remove player
        synchronized (this){
            playersTimers.get(nickname).cancel();
            playersTimers.get(nickname).purge();
        }
        removePlayer(nickname);

        commandResult = CommandResult.SUCCESS;
    }

    public void joinNewGame(String nickname, TokenColor tokenColor, int playersNumber) {
        // this command can always be used
        Player player = getPendingPlayer(nickname);
        assert(player != null);
        int gameId;
        try{
            gameId = createGame(playersNumber);
        }catch(WrongNumberOfPlayersException e) {
            commandResult = CommandResult.WRONG_PLAYERS_NUMBER;
            VirtualView virtualView = getVirtualView(nickname);
            if(virtualView == null) { //TODO è necessario? non vengono inserite VirtualView null nella mappa. Usare assert?
                throw new RuntimeException();
            }
            try {
                virtualView.notifyJoinNotSuccessful();
            } catch (RemoteException ex) {
                //TODO
                throw new RuntimeException(ex);
            }
            return;
        }
        for(GameController gameController : gameControllers) {
            if(gameController.getId() == gameId) {
                RmiServerGamesManager.getRmiServerGamesManager().createServerGame(gameId);
                VirtualView virtualView = getVirtualView(nickname);
                if(virtualView == null) { //TODO come sopra per la mappa
                    throw new RuntimeException();
                }
                try {
                    virtualView.setServerGame(gameId);
                } catch (RemoteException ex) {
                    //TODO
                    throw new RuntimeException(ex);
                }
                // no need to check the token color for the first player of the gameController
                player.setTokenColor(tokenColor);
                gameController.addPlayer(player, playerVirtualViews.get(nickname));
            }
        }
        // remove player
        synchronized (this){
            playersTimers.get(nickname).cancel();
            playersTimers.get(nickname).purge();
        }
        removePlayer(nickname);
        commandResult = CommandResult.SUCCESS;
    }

    /**
     * Method that creates a new GameController and adds it to the list gameControllers.
     * @param playersNumber number of player of the new game, decided by the first player to join.
     */
    private int createGame(int playersNumber) throws WrongNumberOfPlayersException {
        // check players number
        if(playersNumber < 2 || playersNumber > 4){
            throw new WrongNumberOfPlayersException();
        }

        // find first free id
        int id = findFirstFreeId();

        // create and shuffle decks
        DrawableDeck<DrawableCard> resourceCardsDeck = DecksBuilder.buildResourceCardsDeck();
        resourceCardsDeck.shuffle();
        DrawableDeck<GoldCard> goldCardsDeck = DecksBuilder.buildGoldCardsDeck();
        goldCardsDeck.shuffle();
        PlayingDeck<ObjectiveCard> objectiveCardDeck = DecksBuilder.buildObjectiveCardsDeck();
        objectiveCardDeck.shuffle();
        Deck<PlaceableCard> starterCardsDeck = DecksBuilder.buildStarterCardsDeck();
        starterCardsDeck.shuffle();

        // create gameController
        GameController gameController = new GameController(id, playersNumber, resourceCardsDeck, goldCardsDeck, objectiveCardDeck, starterCardsDeck);
        gameControllers.add(gameController);

        return id;
    }

    /**
     * Method that finds the first free id.
     * @return first free id
     */
    private int findFirstFreeId() {
        boolean foundId = false;
        boolean foundGame;
        int id = 0;
        while(!foundId){
            foundGame = false;
            for(GameController g: gameControllers) {
                if(g.getId() == id){
                    foundGame = true;
                }
            }
            if(!foundGame){
                foundId = true;
            }
            else{
                id++;
            }
        }
        return id;
    }

    public void displayExistingGames(String nickname) {
        Player player = getPendingPlayer(nickname);
        assert(player != null);
        VirtualView virtualView = getVirtualView(nickname);
        assert(virtualView != null); //TODO sarebbe la riga che sostituisce if nei TODO precedenti
        ExistingGamesUpdate update = new ExistingGamesUpdate(getFreeGamesDetails());
        try{
            virtualView.receiveExistingGamesUpdate(update);
        }catch(RemoteException e) {
            // TODO
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public Map<Integer, Integer> getFreeGamesDetails() {
        Map<Integer, Integer> gameDetails = new HashMap<>();
        for(GameController g: gameControllers) {
            if(g.getState().equals(GameState.GAME_STARTING)) {
                gameDetails.put(g.getId(), g.getPlayersNumber());
            }
        }
        return gameDetails;
    }

    public synchronized void deleteGame(int gameId) {
        GameController gameController = null;
        for(GameController g: gameControllers) {
            if(g.getId() == gameId) {
                gameController = g;
            }
        }
        if(gameController != null && gameController.getState().equals(GameState.GAME_ENDED)){
            gameControllers.remove(gameController);
        }
    }

    private void removePlayer(String nickname) {
        Player player = getPendingPlayer(nickname);
        pendingPlayers.remove(player);
        playerVirtualViews.remove(nickname);
        playersTimers.remove(nickname);
        System.out.println("player rimosso dai pending");
    }
}

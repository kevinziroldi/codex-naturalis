package it.polimi.ingsw.gc07.model_view;

import it.polimi.ingsw.gc07.model.GameState;
import it.polimi.ingsw.gc07.model.CardType;
import it.polimi.ingsw.gc07.model.TokenColor;
import it.polimi.ingsw.gc07.model.cards.DrawableCard;
import it.polimi.ingsw.gc07.model.cards.GoldCard;
import it.polimi.ingsw.gc07.model.cards.ObjectiveCard;
import it.polimi.ingsw.gc07.model.cards.PlaceableCard;
import it.polimi.ingsw.gc07.model.chat.ChatMessage;
import it.polimi.ingsw.gc07.model.CommandResult;
import it.polimi.ingsw.gc07.model_view_listeners.GameFieldViewListener;
import it.polimi.ingsw.gc07.model_view_listeners.GameViewListener;
import it.polimi.ingsw.gc07.model_view_listeners.PlayerViewListener;
import it.polimi.ingsw.gc07.view.Ui;

import java.util.*;

/**
 * General client's copy of the game model, contains general info about the game and
 * the reference to other model view elements.
 */
public class GameView {
    /**
     * Nickname of the owner of the game view.
     */
    private final String ownerNickname;
    /**
     * ID of the game.
     */
    private int id;
    /**
     * State of the game.
     */
    private GameState state;
    /**
     * List of winner(s) of the game.
     */
    private List<String> winners;
    /**
     * Integer value representing the position of the current player.
     */
    private int currPlayer;
    /**
     * Boolean attribute, true if a player has reached 20 points.
     */
    private boolean penultimateRound;
    /**
     * Boolean attribute, if it is the additional round of the game.
     */
    private boolean additionalRound;
    /**
     * Command result.
     */
    private CommandResult commandResult;
    /**
     * BoardView reference.
     */
    private final BoardView boardView;
    /**
     * DeckView reference.
     */
    private final DeckView deckView;
    /**
     * ChatView reference.
     */
    private final ChatView chatView;
    /**
     * List of PlayerViews.
     * Same order of the list of players on the server.
     */
    private final List<PlayerView> playerViews;
    /**
     * List of game view listeners.
     */
    private final List<GameViewListener> gameViewListeners;

    /**
     * Constructor of the class GameView
     */
    public GameView(String ownerNickname) {
        this.ownerNickname = ownerNickname;
        this.id = -1;
        this.winners = null;
        this.boardView = new BoardView();
        this.deckView = new DeckView();
        this.chatView = new ChatView();
        this.playerViews = new ArrayList<>();
        this.gameViewListeners = new ArrayList<>();
    }

    /**
     * Method used to add a view listener to game view components.
     * @param uiListener view listener
     */
    public void addViewListener(Ui uiListener) {
        gameViewListeners.add(uiListener);
        boardView.addListener(uiListener);
        chatView.addListener(uiListener);
        deckView.addListener(uiListener);
    }

    // getters

    /**
     * Getter for game id.
     * @return game id
     */
    public int getId() {
        return id;
    }

    /**
     * Getter method for game state.
     * @return game state
     */
    public synchronized GameState getGameState() {
        return state;
    }

    /**
     * Getter method for the current player.
     * @return current player nickname
     */
    public synchronized String getCurrentPlayerNickname() {
        String currPlayerNickname;
        if(currPlayer >= 0 && currPlayer < playerViews.size()) {
            currPlayerNickname = playerViews.get(currPlayer).getNickname();
        }else {
            currPlayerNickname = null;
        }
        return currPlayerNickname;
    }

    /**
     * Getter method for owner nickname.
     * @return owner nickname
     */
    public String getOwnerNickname() {
        return ownerNickname;
    }

    /**
     * Getter method for common objective.
     * @return common objective cards
     */
    public List<ObjectiveCard> getCommonObjective() {
        return deckView.getCommonObjective();
    }

    /**
     * Getter method for top resource card.
     * @return top resource card
     */
    public DrawableCard getTopResourceDeck() {
        return deckView.getTopResourceDeck();
    }

    /**
     * Getter method for top gold card.
     * @return top gold card
     */
    public GoldCard getTopGoldDeck() {
        return deckView.getTopGoldDeck();
    }

    /**
     * Getter method for face up resource cards.
     * @return face up resource cards
     */
    public List<DrawableCard> getFaceUpResourceCard() {
        return deckView.getFaceUpResourceCard();
    }

    /**
     * Getter method for face up gold cards.
     * @return face up gold cards
     */
    public List<GoldCard> getFaceUpGoldCard() {
        return deckView.getFaceUpGoldCard();
    }

    /**
     * Getter method for owner's messages.
     * @return owner's messages
     */
    public synchronized List<ChatMessage> getOwnerMessages() {
        return chatView.getMessages();
    }

    /**
     * Method that allows to set game model info.
     * @param id id
     * @param state state
     * @param currPlayer current player
     * @param penultimateRound twentyPointsReached
     * @param additionalRound additionalRound
     */
    public synchronized void setGameModel(int id, GameState state, int currPlayer, boolean penultimateRound, boolean additionalRound) {
        this.id = id;
        for(GameViewListener l: gameViewListeners) {
            l.receiveGameIdUpdate(this.id);
        }
        this.state = state;
        this.currPlayer = currPlayer;
        if(!this.penultimateRound && penultimateRound) {
            for(GameViewListener l : gameViewListeners) {
                l.receivePenultimateRoundUpdate();
            }
        }
        this.penultimateRound = penultimateRound;

        if(!this.additionalRound && additionalRound) {
            for(GameViewListener l : gameViewListeners) {
                l.receiveAdditionalRoundUpdate();
            }
        }
        this.additionalRound = additionalRound;

        // send general model update
        for(GameViewListener l: gameViewListeners) {
            l.receiveGeneralModelUpdate(state, getCurrentPlayerNickname());
            // for tui, it may print decks
        }
    }

    /**
     * Setter method for winners.
     * @param winners winners
     */
    public synchronized void setWinners(List<String> winners) {
        if (winners == null) {
            return;
        }
        this.winners = winners;
        for(GameViewListener l: gameViewListeners) {
            l.receiveWinnersUpdate(winners);
        }
    }

    /**
     * Method that adds a new chat message to the ChatView.
     * @param chatMessage new chat message
     */
    public synchronized void addMessage(ChatMessage chatMessage) {
        if(chatMessage.getIsPublic() || chatMessage.isForReceiver(ownerNickname) || chatMessage.getSender().equals(ownerNickname)) {
            chatView.addMessage(chatMessage);
        }
    }

    /**
     * Method used to set the full chat content after a reconnection.
     * @param chatMessages full chat content
     */
    public synchronized void setChatMessages(List<ChatMessage> chatMessages) {
        chatView.setChatMessages(chatMessages);
    }

    /**
     * Method used to save a new deck update
     * @param topResourceCard top resource card
     * @param topGoldCard top gold card
     * @param resourceFaceUpCards face up resource cards
     * @param goldFaceUpCards face up gold cards
     * @param commonObjective common objective
     */
    public synchronized void setDeck(DrawableCard topResourceCard, GoldCard topGoldCard, List<DrawableCard> resourceFaceUpCards, List<GoldCard> goldFaceUpCards, List<ObjectiveCard> commonObjective) {
        deckView.setTopResourceDeck(topResourceCard);
        deckView.setTopGoldDeck(topGoldCard);
        deckView.setFaceUpResourceCard(resourceFaceUpCards);
        deckView.setFaceUpGoldCard(goldFaceUpCards);
        deckView.setCommonObjective(commonObjective);
    }

    /**
     * Method that allows to set the starter card for the owner of the game view.
     * Starter cards of other players will be visible once placed.
     * @param nickname nickname
     * @param starterCard starter card
     */
    public synchronized void setStarterCard(String nickname, PlaceableCard starterCard) {
        if(ownerNickname.equals(nickname)) {
            for(PlayerView playerView: playerViews) {
                if(playerView.getNickname().equals(nickname)) {
                    playerView.setStarterCard(starterCard);
                }
            }
        }
        // else, don't save it
    }

    /**
     * Method that allows to set the objective cards for the owner of the game view.
     * @param nickname nickname
     * @param objectiveCards objective cards
     */
    public synchronized void setSecretObjectives(String nickname, List<ObjectiveCard> objectiveCards) {
        if(ownerNickname.equals(nickname)) {
            for(PlayerView playerView: playerViews) {
                if(playerView.getNickname().equals(nickname)) {
                    playerView.setSecretObjectives(nickname, objectiveCards);
                }
            }
        }
        // else, don't save it
    }

    /**
     * Method that allows to set a new card in the game field.
     * @param nickname nickname of the player who placed the card
     * @param card card
     * @param x x
     * @param y y
     * @param way way
     * @param orderPosition order position
     */
    public synchronized void addCard(String nickname, PlaceableCard card, int x, int y, boolean way, int orderPosition) {
        // add the card to the specified game field view specified
        for(PlayerView playerView: playerViews) {
            if(playerView.getNickname().equals(nickname)) {
                playerView.addCard(card, x, y, way, orderPosition);
            }
        }
    }

    /**
     * Method used to receive a full game field update after a reconnection.
     * @param nickname nickname
     * @param starterCard starter card
     * @param cardsContent cards content matrix
     * @param cardsFace cards face matrix
     * @param cardsOrder cards order matrix
     */
    public synchronized void receiveFullGameFieldUpdate(String nickname, PlaceableCard starterCard, PlaceableCard[][] cardsContent, Boolean[][] cardsFace, int[][] cardsOrder) {
        for(PlayerView playerView: playerViews) {
            if(playerView.getNickname().equals(nickname)) {
                playerView.setFullGameField(nickname,starterCard, cardsContent, cardsFace, cardsOrder);
            }
        }
    }

    /**
     * Method that allows to set a new score for the player.
     * @param nickname nickname
     * @param newScore score
     */
    public void setNewScore(String nickname, int newScore) {
        boardView.setNewScore(nickname, newScore);
    }

    /**
     * Method to set a value for isStalled.
     * @param nickname nickname
     * @param isStalled isStalled value
     */
    public synchronized void setIsStalled(String nickname, boolean isStalled) {
        for(PlayerView p: playerViews) {
            if(p.getNickname().equals(nickname)) {
                p.setIsStalled(isStalled);
            }
        }
    }

    /**
     * Method to set a value for isConnected.
     * @param nickname nickname
     * @param isConnected isConnected value
     */
    public synchronized void setIsConnected(String nickname, boolean isConnected) {
        for(PlayerView p: playerViews) {
            if(p.getNickname().equals(nickname)) {
                p.setIsConnected(isConnected);
            }
        }
    }

    /**
     * Method to set the card hand of a player.
     * @param nickname nickname
     * @param newHand card hand
     */
    public synchronized void setCardHand(String nickname, List<DrawableCard> newHand, List<ObjectiveCard> personalObjectives) {
        for(PlayerView p: playerViews) {
            if(p.getNickname().equals(nickname)) {
                p.setCardHand(newHand, personalObjectives);
            }
        }
    }

    /**
     * Method that allows to add a new player view.
     * @param newPlayerViews new list of player views
     */
    public synchronized void setPlayerViews(List<PlayerView> newPlayerViews) {
        for(PlayerView playerView: newPlayerViews) {
            boolean found = false;
            for(PlayerView myPlayerView: this.playerViews) {
                if(playerView.getNickname().equals(myPlayerView.getNickname())) {
                    found = true;
                    break;
                }
            }
            if(!found) {
                this.playerViews.add(playerView);
                boardView.addPlayerToBoard(playerView.getNickname(), playerView.getTokenColor());
                playerView.addListener((PlayerViewListener) gameViewListeners.getFirst());
                playerView.addGameFieldListener((GameFieldViewListener) gameViewListeners.getFirst());
            }
        }
        boardView.updateListeners();

        Map<String, TokenColor> nicknames = new HashMap<>();
        Map<String, Boolean> connectionValues = new HashMap<>();
        Map<String, Boolean> stallValues = new HashMap<>();
        for(PlayerView p: this.playerViews) {
            nicknames.put(p.getNickname(), p.getTokenColor());
            connectionValues.put(p.getNickname(), p.isConnected());
            stallValues.put(p.getNickname(), p.isStalled());
        }
        for(GameViewListener l: gameViewListeners) {
            l.receivePlayersUpdate(nicknames, connectionValues, stallValues);
        }
    }

    /**
     * Method to set a command result.
     * @param commandResult command result
     */
    public void setCommandResult(String nickname, CommandResult commandResult) {
        this.commandResult = commandResult;

        // update view
        if(nickname.equals(ownerNickname) && !commandResult.equals(CommandResult.SUCCESS)) {
            for(GameViewListener l: gameViewListeners) {
                l.receiveCommandResultUpdate(this.commandResult);
            }
        }
    }

    /**
     * Method used to receive existing games.
     * @param existingGamesPlayerNumber existing games players number
     * @param existingGamesTokenColor existing games token colors
     */
    public void displayExistingGames(Map<Integer, Integer> existingGamesPlayerNumber, Map<Integer, List<TokenColor>> existingGamesTokenColor) {
        for(GameViewListener l: gameViewListeners) {
            l.receiveExistingGamesUpdate(existingGamesPlayerNumber, existingGamesTokenColor);
        }
    }

    /**
     * Method used to check if a player is in the game.
     * @param nickname nickname
     * @return true if the player is in the game
     */
    public synchronized boolean checkPlayerPresent(String nickname) {
        for(PlayerView p: playerViews) {
            if(p.getNickname().equals(nickname)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Method used to check if a player is the current one.
     * @param nickname nickname
     * @return true if the player is the current one
     */
    public synchronized boolean isCurrentPlayer(String nickname) {
        if(currPlayer < 0)
            return false;
        for(int i = 0; i < playerViews.size(); i++) {
            if (playerViews.get(i) == null) {
                continue;
            }
            if(playerViews.get(i).getNickname().equals(nickname) && i == currPlayer)
                return true;
        }
        return false;
    }

    /**
     * Getter method for the number of face up cards.
     * @param cardType card type
     * @return number of face up cards
     */
    public synchronized int getNumFaceUpCards(CardType cardType) {
        return deckView.getNumFaceUpCards(cardType);
    }

    /**
     * Getter method for the owner's current hand size.
     * @return owner's current hand size
     */
    public synchronized int getCurrHandSize() {
        for(PlayerView p: playerViews) {
            if(p.getNickname().equals(ownerNickname)) {
                return p.getCurrHandSize();
            }
        }
        return -1;
    }

    /**
     * Getter method for game field dimension.
     * @return game field dimension
     */
    public synchronized int getGameFieldDim() {
        for(PlayerView p: playerViews) {
            if(p.getNickname().equals(ownerNickname)) {
                return p.getGameField().getDim();
            }
        }
        return -1;
    }

    /**
     * Getter method for a player's game field.
     * @param nickname nickname
     * @return game field
     */
    public synchronized GameFieldView getGameField(String nickname) {
        PlayerView player = null;
        for(PlayerView p: playerViews) {
            if(p.getNickname().equals(nickname)) {
                player = p;
            }
        }
        assert(player != null);
        return player.getGameField();
    }
}

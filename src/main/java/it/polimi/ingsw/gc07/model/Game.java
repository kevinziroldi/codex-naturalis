package it.polimi.ingsw.gc07.model;

import it.polimi.ingsw.gc07.exceptions.CardNotPresentException;
import it.polimi.ingsw.gc07.exceptions.PlayerAlreadyPresentException;
import it.polimi.ingsw.gc07.model.cards.Card;
import it.polimi.ingsw.gc07.model.cards.NonStarterCard;
import it.polimi.ingsw.gc07.model.cards.ObjectiveCard;
import it.polimi.ingsw.gc07.model.decks.Deck;
import it.polimi.ingsw.gc07.model.enumerations.TokenColor;

import java.util.ArrayList;
import java.util.List;

public class Game {
    /**
     * Id of the game.
     */
    //TODO Serve?
    private int gameId;
    /**
     * Number of players in the game, chose by the first player.
     */
    private int playersNumber;
    /**
     * List of players in the game.
     */
    private List<Player> players;
    /**
     * Integer value representing the index of the current player in the List
     */
    private int currPlayer;
    /**
     * Score track board of the game.
     */
    private ScoreTrackBoard scoreTrackBoard;
    /**
     * Deck of resource cards.
     */
    private Deck resourceCardsDeck;
    /**
     * Deck of gold cards.
     */
    private Deck goldCardsDeck;
    /**
     * Deck of objective cards.
     */
    private Deck objectiveCardsDeck;
    /**
     * Deck of starter cards.
     */
    private Deck starterCardsDeck;

    /**
     * Constructor of a Game with only the first player.
     * @param gameId id of the game
     * @param playersNumber number of players
     * @param resourceCardsDeck deck of resource cards
     * @param goldCardsDeck deck of gold cards
     * @param objectiveCardsDeck deck of objective cards
     * @param starterCardsDeck deck of starter cards
     * @param nickname player to add to the game
     * @param tokenColor color of player's token
     * @param connectionType type of connection
     */
    public Game(int gameId, int playersNumber, Deck resourceCardsDeck,
                Deck goldCardsDeck, Deck objectiveCardsDeck, Deck starterCardsDeck,
                String nickname, TokenColor tokenColor, boolean connectionType) {
        this.gameId = gameId;
        // TODO: mettiamo un'eccezione per playersNumber?
        this.playersNumber = playersNumber;
        this.players = new ArrayList<>();
        this.scoreTrackBoard = new ScoreTrackBoard();
        this.resourceCardsDeck = resourceCardsDeck;
        this.goldCardsDeck = goldCardsDeck;
        this.objectiveCardsDeck = objectiveCardsDeck;
        this.starterCardsDeck = starterCardsDeck;
        addPlayer(nickname, tokenColor, connectionType);
    }

    /**
     * Method to add a new player.
     * @param nickname player to add to the game
     * @param tokenColor color of player's token
     * @param connectionType type of connection
     */
    public void addPlayer(String nickname, TokenColor tokenColor, boolean connectionType) {
        try{
            List<NonStarterCard> currentHand = new ArrayList<>();
            // TODO: aggiungere le carte alla currentHand
            ObjectiveCard secretObjective = (ObjectiveCard) objectiveCardsDeck.drawDeckCard();
            Player newPlayer = new Player(nickname, tokenColor, currentHand, secretObjective, connectionType);
            // TODO
            // Aggiunge Player alla lista e allo ScoreTrackBoard
            // Controlla se è ultimo, se sì, scegliere il primo giocatore a caso e
            // modifica il suo attributo isFirst e lo mette come currPlayer
        }
        catch(CardNotPresentException e){
            e.printStackTrace();
        }
    }

    /**
     * Method telling if there are available places in the game.
     * @return true if no other player can connect to the game
     */
    public boolean isFull(){
        return players.size() == playersNumber;
    }

    public void disconnectPlayer(Player player){
        //TODO: disconnect the player
    }

    public void reconnectPlayer(Player player){
        // TODO: reconnect the player
    }
}

package it.polimi.ingsw.gc07.model_view;

import it.polimi.ingsw.gc07.model.cards.PlaceableCard;
import it.polimi.ingsw.gc07.model_view_listeners.GameFieldViewListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GameFieldView implements Serializable {
    /**
     * Player's starter card.
     */
    private PlaceableCard starterCard;
    /**
     * Each cell of the matrix contains a placeable card, or null if the place is empty.
     */
    private final PlaceableCard[][] cardsContent;
    /**
     * Each cell of the matrix contains a boolean value:
     * false: the card has been placed face up
     * true: the card has been placed face down
     */
    private final Boolean[][] cardsFace;
    /**
     * Matrix representing the placement order of cards.
     */
    private final int [][] cardsOrder;
    /**
     * Constant value representing the max dimension of a player's game field.
     */
    private static final int dim = 81;
    /**
     * List of GameFieldView listeners.
     */
    private final List<GameFieldViewListener> gameFieldViewListeners;

    /**
     * Constructor of the game field view.
     */
    public GameFieldView() {
        this.starterCard = null;
        this.cardsContent = new PlaceableCard[dim][dim];
        for(int i=0; i < dim; i++){
            for(int j=0; j < dim; j++){
                this.cardsContent[i][j] = null;
            }
        }
        this.cardsFace = new Boolean[dim][dim];
        for(int i = 0; i < dim; i++){
            for(int j = 0; j < dim; j++){
                this.cardsFace[i][j] = null;
            }
        }
        this.cardsOrder = new int[dim][dim];
        for(int i=0; i < dim; i++){
            for(int j=0; j < dim; j++){
                this.cardsOrder[i][j] = 0;
            }
        }
        this.gameFieldViewListeners = new ArrayList<>();
    }

    public void setFullGameField(PlaceableCard starterCard, PlaceableCard[][] cardsContent, Boolean[][] cardsFace, int[][] cardsOrder) {
        this.starterCard = starterCard;
        for(int i=0; i < dim; i++){
            for(int j=0; j < dim; j++){
                this.cardsContent[i][j] = cardsContent[i][j];
            }
        }
        for(int i = 0; i < dim; i++){
            for(int j = 0; j < dim; j++){
                this.cardsFace[i][j] = cardsFace[i][j];
            }
        }
        for(int i=0; i < dim; i++){
            for(int j=0; j < dim; j++){
                this.cardsOrder[i][j] = cardsOrder[i][j];
            }
        }
    }

    /**
     * Method to register a game field view listener.
     * @param gameFieldViewListener game field view listener
     */
    public void addListener(GameFieldViewListener gameFieldViewListener) {
        gameFieldViewListeners.add(gameFieldViewListener);
    }

    public PlaceableCard[][] getCardsContent() {
        return cardsContent;
    }

    public Boolean[][] getCardsFace() {
        return cardsFace;
    }

    public int[][] getCardsOrder() {
        return cardsOrder;
    }

    /**
     * Setter method for the starter card.
     * @param starterCard starter card
     */
    public void setStarterCard(PlaceableCard starterCard) {
        this.starterCard = starterCard;
    }

    /**
     * Method to add a new card to the game field view.
     * @param nickname nickname
     * @param card new card
     * @param x x
     * @param y y
     * @param way way
     * @param orderPosition order position
     */
    public void addCard(String nickname, PlaceableCard card, int x, int y, boolean way, int orderPosition) {
        cardsContent[x][y] = card;
        cardsFace[x][y] = way;
        cardsOrder[x][y] = orderPosition;
        for(GameFieldViewListener l: gameFieldViewListeners) {
            l.receiveGameFieldUpdate(nickname, cardsContent, cardsFace, cardsOrder);
        }
    }

    public int getDim() {
        return dim;
    }
}

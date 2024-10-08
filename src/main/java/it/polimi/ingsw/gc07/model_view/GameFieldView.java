package it.polimi.ingsw.gc07.model_view;

import it.polimi.ingsw.gc07.model.cards.PlaceableCard;
import it.polimi.ingsw.gc07.model_view_listeners.GameFieldViewListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Client's copy of model GameField.
 */
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

    /**
     * Copy constructor of the game field view.
     */
    public GameFieldView(GameFieldView gameFieldView){
        this.starterCard = gameFieldView.starterCard;
        // getters return a copy
        this.cardsContent = gameFieldView.getCardsContent();
        this.cardsFace = gameFieldView.getCardsFace();
        this.cardsOrder = gameFieldView.getCardsOrder();
        this.gameFieldViewListeners = new ArrayList<>();
    }

    /**
     * Setter method for game field structures.
     *  @param nickname nickname
     * @param starterCard starter card
     * @param cardsContent card content matrix
     * @param cardsFace card face matrix
     * @param cardsOrder card order matrix
     */
    public void setFullGameField(String nickname, PlaceableCard starterCard, PlaceableCard[][] cardsContent, Boolean[][] cardsFace, int[][] cardsOrder) {
        if (nickname == null || starterCard == null || cardsContent == null || cardsFace == null || cardsOrder == null) {
            return;
        }
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
        notifyListeners(nickname);
    }

    /**
     * Method to register a game field view listener.
     * @param gameFieldViewListener game field view listener
     */
    public synchronized void addListener(GameFieldViewListener gameFieldViewListener) {
        gameFieldViewListeners.add(gameFieldViewListener);
    }

    /**
     * Getter method for cards content matrix.
     * @return cards content matrix
     */
    public PlaceableCard[][] getCardsContent() {
        PlaceableCard[][] cardsContentCopy = new PlaceableCard[dim][dim];
        for(int i = 0; i < dim; i++){
            for(int j = 0; j < dim; j++){
                cardsContentCopy[i][j] = cardsContent[i][j];
            }
        }
        return cardsContentCopy;
    }

    /**
     * Getter method for cards face matrix.
     * @return cards face matrix
     */
    public Boolean[][] getCardsFace() {
        Boolean[][] cardsFaceCopy = new Boolean[dim][dim];
        for(int i = 0; i < dim; i++){
            for(int j = 0; j < dim; j++){
                cardsFaceCopy[i][j] = cardsFace[i][j];
            }
        }
        return cardsFaceCopy;
    }

    /**
     * Getter method for cards order matrix.
     * @return cards order matrix
     */
    public int[][] getCardsOrder() {
        int [][] cardsOrderCopy = new int[dim][dim];
        for(int i = 0; i < dim; i++){
            for(int j = 0; j < dim; j++){
                cardsOrderCopy[i][j] = cardsOrder[i][j];
            }
        }
        return cardsOrderCopy;
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
        if (nickname == null || card == null) {
            return;
        }
        cardsContent[x][y] = card;
        cardsFace[x][y] = way;
        cardsOrder[x][y] = orderPosition;

        // create copy for update
        notifyListeners(nickname);
    }

    /**
     * Private method used to notify all game field listeners (util).
     * @param nickname game field owner's nickname
     */
    private synchronized void notifyListeners(String nickname){
        PlaceableCard[][] cardsContentCopy = getCardsContent();
        Boolean[][] cardsFaceCopy = getCardsFace();
        int [][] cardsOrderCopy = getCardsOrder();
        for(GameFieldViewListener l: gameFieldViewListeners) {
            l.receiveGameFieldUpdate(nickname, cardsContentCopy, cardsFaceCopy, cardsOrderCopy);
        }
    }
    /**
     * Getter method for dimension.
     * @return dimension of the game field
     */
    public int getDim() {
        return dim;
    }

    /**
     * Getter method for the starter card.
     * @return starter card
     */
    public PlaceableCard getStarterCard() {
        return starterCard;
    }
}

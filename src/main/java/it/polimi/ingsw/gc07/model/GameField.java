package it.polimi.ingsw.gc07.model;

import it.polimi.ingsw.gc07.exceptions.CardAlreadyPresentException;
import it.polimi.ingsw.gc07.exceptions.CardNotPresentException;
import it.polimi.ingsw.gc07.model.cards.Card;
import it.polimi.ingsw.gc07.model.cards.PlaceableCard;

/**
 * Class representing the game field of a single player,
 * it contains all the cards the player has placed,
 * including both their position and the way cards are placed.
 */
public class GameField {
    /**
     * Matrix of dimension 81x81, the biggest possible dimension
     * for a player's game field. Each cell contains a placeable card,
     * or null if the place is empty.
     */
    private PlaceableCard[][] cardsContent;

    /**
     * Matrix of dimension 81x81, the biggest possible dimension
     * for a player's game field.
     * false: the card has been placed face up
     * true: the card has been placed face down
     */
    private boolean[][] cardsFace;

    /**
     * Constant value representing the max dimension of a player's
     * game field.
     * A player can place, at most, 40 card in one direction starting
     * from position (40,40), which is the position of the starter card.
     */
    private static final int dim = 81;

    /**
     * Constructor of the game field: builds an empty game field.
     */
    public GameField() {
        this.cardsContent = new PlaceableCard[dim][dim];
        for(int i=0; i < dim; i++){
            for(int j=0; j < dim; j++){
                this.cardsContent[i][j] = null;
            }
        }
        this.cardsFace = new boolean[dim][dim];
        for(int i = 0; i < dim; i++){
            for(int j = 0; j < dim; j++){
                this.cardsFace[i][j] = false;
            }
        }
    }

    /**
     * Constructor of the game field: builds a copy of an existing game field.
     */
    public GameField(GameField existingGameField) {
        this.cardsContent = new PlaceableCard[dim][dim];
        for(int i=0; i < dim; i++){
            for(int j=0; j < dim; j++){
                this.cardsContent[i][j] = existingGameField.cardsContent[i][j];
            }
        }
        this.cardsFace = new boolean[dim][dim];
        for(int i=0; i < dim; i++){
            for(int j=0; j < dim; j++){
                this.cardsFace[i][j] = existingGameField.cardsFace[i][j];
            }
        }
    }

    public int getDim(){
        return GameField.dim;
    }

    /**
     * Allows the player to place a card at index (x,y) and
     * in a certain way, face up or face down.
     * @param card card the player wants to place
     * @param x x index of the matrix
     * @param y y index of the matrix
     * @param way false: the card is placed face up, true: the
     *            card is placed face down
     */
    public void placeCard(PlaceableCard card, int x, int y, boolean way) throws IndexOutOfBoundsException,
            NullPointerException, CardAlreadyPresentException {
        if(x < 0 || x >= dim || y <0 || y >= dim) {
            throw new IndexOutOfBoundsException();
        }
        if(card == null){
            throw new NullPointerException();
        }
        if(cardsContent[x][y] != null){
            throw new CardAlreadyPresentException();
        }
        // PlaceableCard is immutable, I can insert the card I receive
        // TODO: manca un macello di roba, prima di inserire la carta
        cardsContent[x][y] = card;
        cardsFace[x][y] = way;
    }

    /**
     * Shows if there is any card in a certain position.
     * @param x x index of the matrix
     * @param y y index of the matrix
     * @return true: there is a card in position (x,y), false: otherwise
     */
    public boolean isCardPresent(int x, int y) throws IndexOutOfBoundsException {
        if(x < 0 || x >= dim || y <0 || y >= dim){
            throw new IndexOutOfBoundsException();
        }
        if(cardsContent[x][y] == null){
            return false;
        }
        return true;
    }

    /**
     * Returns the card placed in position (x,y).
     * @param x x index of the matrix
     * @param y y index of the matrix
     * @return card in position (x,y)
     */
    public PlaceableCard getPlacedCard(int x, int y) throws IndexOutOfBoundsException, CardNotPresentException {
        if(x < 0 || x >= dim || y <0 || y >= dim){
            throw new IndexOutOfBoundsException();
        }
        if(cardsContent[x][y] == null){
            throw new CardNotPresentException();
        }
        // PlaceableCard is immutable, I can return the card without copy
        return cardsContent[x][y];
    }

    /**
     * Returns a boolean representing the way a card is placed.
     * @param x x index of the matrix
     * @param y y index of the matrix
     * @return  false: the card has been placed face up
     *          true: the card has been placed face down
     */
    public boolean getCardWay(int x, int y) throws IndexOutOfBoundsException, CardNotPresentException {
        if(x < 0 || x >= dim || y <0 || y >= dim){
            throw new IndexOutOfBoundsException();
        }
        if(cardsContent[x][y] == null){
            throw new CardNotPresentException();
        }
        return cardsFace[x][y];
    }
}

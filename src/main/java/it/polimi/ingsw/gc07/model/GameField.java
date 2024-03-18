package it.polimi.ingsw.gc07.model;

import it.polimi.ingsw.gc07.exceptions.CardAlreadyPresentException;
import it.polimi.ingsw.gc07.exceptions.CardNotPresentException;
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
     * Matrix of dimension 81x81, biggest possible dimension for a player's
     * game field. Represent the placement's order of the cards.
     */
    private int [][] cardsOrder;

    /**
     * Integer attribute that show the number of cards played in the game field
     */
    private int numPlayedCards;
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
        this.cardsOrder = new int [dim][dim];
        for(int i=0; i < dim; i++){
            for(int j=0; j < dim; j++){
                this.cardsOrder[i][j] = 0;
            }
        }
        this.numPlayedCards = 0;
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
        this.cardsOrder = new int [dim][dim];
        for(int i=0; i < dim; i++){
            for(int j=0; j < dim; j++){
                this.cardsOrder[i][j] = existingGameField.cardsOrder[i][j];
            }
        }
        this.numPlayedCards = existingGameField.numPlayedCards;
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
            NullPointerException, CardAlreadyPresentException { //aggiungere CardNotPlaceableException
        if(x < 0 || x >= dim || y <0 || y >= dim) {
            throw new IndexOutOfBoundsException();
        }
        if(card == null){
            throw new NullPointerException();
        }
        if(cardsContent[x][y] != null){
            throw new CardAlreadyPresentException();
        }
        if(numPlayedCards == 0){
            //the first card must be a StarterCard, if so no further condition must be checked
            if(card.getBackCorners() == null){
                //throw new CardNotPlaceableException();
            }
        }else{
            //this is not the first placement, this card cannot be a StarterCard
            if(card.getBackCorners() != null){
                //throw new CardNotPlaceableException();
            }
            //checking if there is at least one available corner near (x,y)
            if(x-1 >= 0){
                if(cardsContent[x-1][y] != null){
                    //the card would cover two corners of the card above it
                    //throw new CardNotPlaceableException();
                }
                if(y-1 >= 0){
                    if(cardsContent[x][y-1] != null){
                        //the card would cover two corners of the card on its left
                        //throw new CardNotPlaceableException();
                    }
                    if(cardsContent[x-1][y-1] != null){
                        //a placed card is present
                        if(cardsFace[x-1][y-1] == false){
                            //the placed card is face up
                            if(cardsContent[x-1][y-1].getFrontCorners()[2] == false){
                                //the needed corner is not available
                                //throw new CardNotPlaceableException();
                            }
                        }//the placed card is face down, the corner needed is available for sure
                    }//the placed card is not present
                    if(x+1 <= 80){
                        if(cardsContent[x+1][y-1] != null){
                            if(cardsFace[x+1][y-1] == false){
                                if(cardsContent[x+1][y-1].getFrontCorners()[1] == false){
                                    //the card to be placed covers an unavailable corner of another card
                                    //throw new CardNotPlaceableException();
                                }
                            }
                        }
                    }
                }
                if(y+1 <= 80){
                    if(cardsContent[x][y+1] != null){
                        //the card would cover two corners of the card on its right
                        //throw new CardNotPlaceableException();
                    }
                    if(cardsContent[x-1][y+1] != null){
                        //a placed card is present
                        if(cardsFace[x-1][y+1] == false){
                            //the placed card is face up
                            if(cardsContent[x-1][y+1].getFrontCorners()[3] == false){
                                //the needed corner is not available
                                //throw new CardNotPlaceableException();
                            }
                        }//the placed card is face down, the corner needed is available for sure
                    }//card in position x-1 y+1 is not present
                    if(x+1 <= 80){
                        if(cardsContent[x+1][y+1] != null){
                            if(cardsFace[x+1][y+1] == false){
                                if(cardsContent[x+1][y+1].getFrontCorners()[0] == false){
                                    //the card to be placed covers an unavailable corner of another card
                                    //throw new CardNotPlaceableException();
                                }
                            }
                        }
                    }
                }
            }
            if(x+1 <= 80){
                if(cardsContent[x+1][y] != null){
                    //the card would cover two corners of the card below it
                    //throw new CardNotPlaceableException();
                }
                if(y-1 >= 0){
                    if(cardsContent[x+1][y-1] != null){
                        //a placed card is present
                        if(cardsFace[x+1][y-1] == false){
                            //the placed card is face up
                            if(cardsContent[x+1][y-1].getFrontCorners()[1] == false){
                                //the needed corner is not available
                                //throw new CardNotPlaceableException();
                            }
                        }//the placed card is face down, the corner needed is available for sure
                    }//card in position x+1 y-1 is not present
                    if(x-1 >= 0){
                        if(cardsContent[x-1][y-1] != null){
                            if(cardsFace[x-1][y-1] == false){
                                if(cardsContent[x-1][y-1].getFrontCorners()[2] == false){
                                    //the card to be placed covers an unavailable corner of another card
                                    //throw new CardNotPlaceableException();
                                }
                            }
                        }
                    }
                }
                if(y+1 <= 80){
                    if(cardsContent[x+1][y+1] != null){
                        //a placed card is present
                        if(cardsFace[x+1][y+1] == false){
                            //the placed card is face up
                            if(cardsContent[x+1][y-1].getFrontCorners()[0] == false){
                                //the needed corner is not available
                                //throw new CardNotPlaceableException();
                            }
                        }//the placed card is face down, the corner needed is available for sure
                    }//card in position x+1 y+1 is not present
                    if(x-1 >= 0){
                        if(cardsContent[x-1][y+1] != null){
                            if(cardsFace[x-1][y+1] == false){
                                if(cardsContent[x-1][y+1].getFrontCorners()[3] == false){
                                    //the card to be placed covers an unavailable corner of another card
                                    //throw new CardNotPlaceableException();
                                }
                            }
                        }
                    }
                }
            }
            if(card.getPlacementCondition().numTimesMet(this) <= 0){
                //the card is a GoldCard and the placement condition is not met
                //throw new CardNotPlaceableException();
            }
        }
        // PlaceableCard is immutable, I can insert the card I receive
        // TODO: manca un macello di roba, prima di inserire la carta
        cardsContent[x][y] = card;
        cardsFace[x][y] = way;
        numPlayedCards++;
        cardsOrder[x][y] = numPlayedCards;
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
     * Method that allows to remove a placed card from the game field.
     * It is used on copies of the game field for verifying conditions,
     * so it does not change the cards' placement order.
     * @param x x index of the matrix
     * @param y y index of the matrix
     * @throws IndexOutOfBoundsException
     * @throws CardNotPresentException
     */
    public void removePlacedCard(int x, int y) throws IndexOutOfBoundsException, CardNotPresentException {
        if(x < 0 || x >= dim || y <0 || y >= dim){
            throw new IndexOutOfBoundsException();
        }
        if(cardsContent[x][y] == null){
            throw new CardNotPresentException();
        }
        cardsContent[x][y] = null;
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

    public int[][] getCardsOrder() {
        int[][] cardsOrderCopy = new int[dim][dim];
        for(int i = 0; i < dim; i++){
            for(int j = 0; j < dim; j++){
                cardsOrderCopy[i][j] = cardsOrder[i][j];
            }
        }
        return cardsOrderCopy;
    }

    public int getNumPlayedCards() {
        return this.numPlayedCards;
    }
}

package it.polimi.ingsw.gc07.model.decks;
import it.polimi.ingsw.gc07.model.enumerations.CardType;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Class representing decks used during the whole game.
 * Objective cards deck is PlayingDeck object.
 */
public class PlayingDeck<T> extends Deck<T> {
    /**
     * List containing face up cards that players can draw.
     */
    List<T> faceUpCards;

    /**
     * Constructor class PlayingDeck.
     * @param type cards type
     * @param content deck content
     */
    public PlayingDeck(CardType type, Stack<T> content) {
        super(type, content);
        this.faceUpCards = new ArrayList<>();
    }

    /**
     * Copy constructor of PlayingDeck.
     * @param existingDeck existing deck
     */
    public PlayingDeck(PlayingDeck<T> existingDeck){
        super(existingDeck);
        this.faceUpCards = new ArrayList<>(existingDeck.faceUpCards);
    }

    public void setUpDeck() {
        addFaceUpCard(drawCard());
        addFaceUpCard(drawCard());

        // TODO listener
    }

    /**
     * Setter method for face up cards.
     * @param faceUpCards list of face up cards
     */
    public void setFaceUpCards(List<T> faceUpCards) {
        this.faceUpCards = new ArrayList<>(faceUpCards);
    }

    /**
     * Method to add a face up card.
     * @param faceUpCard face up card to add
     */
    public void addFaceUpCard(T faceUpCard) {
        this.faceUpCards.add(faceUpCard);
    }

    /**
     * Method that allows a player to see the card in position cardPos
     * without drawing it.
     * @param cardPos position of the card the player wants to see
     * @return face up card in position cardPos
     */
    public T revealFaceUpCard(int cardPos) {
        if(cardPos >= faceUpCards.size()){
            return null;
        }
        return faceUpCards.get(cardPos);
    }
}

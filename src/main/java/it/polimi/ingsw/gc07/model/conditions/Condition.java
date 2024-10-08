package it.polimi.ingsw.gc07.model.conditions;

import it.polimi.ingsw.gc07.model.GameField;

import java.io.Serializable;

/**
 * Interface representing conditions found on game cards: these conditions can
 * be placing conditions or scoring conditions.
 */
public interface Condition extends Serializable {
    /**
     * Method allowing to know how many times a condition is met.
     * @param gameField current game field
     * @return number of times a condition is met
     */
    int numTimesMet(GameField gameField);
}

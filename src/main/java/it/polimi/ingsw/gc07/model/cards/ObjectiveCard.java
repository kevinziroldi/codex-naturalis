package it.polimi.ingsw.gc07.model.cards;

import it.polimi.ingsw.gc07.model.GameField;
import it.polimi.ingsw.gc07.model.CardType;

import it.polimi.ingsw.gc07.model.conditions.*;

/**
 * Class that represents objective cards of the game.
 */
public final class ObjectiveCard extends Card {
    /**
     * Attribute that shows the condition of the objectiveCard.
     */
    private final Condition scoringCondition;
    /**
     * Attribute that shows how many points will the player score if he
     * achieves the objective.
     * The player will score:
     * ("objectiveScore" points) x (the number of times the scoring condition is met)
     */
    private final int objectiveScore;

    /**
     * Constructor of the class ObjectiveCard
     * @param cardID : id of the card
     * @param scoringCondition : scoring condition of the card
     * @param objectiveScore : points that can be obtained
     */
    public ObjectiveCard(int cardID, Condition scoringCondition, int objectiveScore) {
        super(cardID, CardType.OBJECTIVE_CARD);
        this.scoringCondition = scoringCondition;
        this.objectiveScore = objectiveScore;
    }

    /**
     * getter method for attribute scoringCondition
     * @return scoringCondition
     */
    public Condition getScoringCondition() {
        return scoringCondition;
    }

    /**
     * getter method for attribute objectiveScore
     * @return objectiveScore
     */
    public int getPoints(){
        return objectiveScore;
    }

    /**
     * Method returning the score gained thanks to the objective card.
     * @param gameField game field
     * @return score
     */
    public int getObjectiveScore(GameField gameField) {
        return scoringCondition.numTimesMet(gameField) * objectiveScore;
    }

    /**
     * Method returning the number of times the scoring condition is met.
     * @param gameField game field
     * @return number of times the scoring condition is met
     */
    public int numTimesScoringConditionMet(GameField gameField) {
        return scoringCondition.numTimesMet(gameField);
    }
}

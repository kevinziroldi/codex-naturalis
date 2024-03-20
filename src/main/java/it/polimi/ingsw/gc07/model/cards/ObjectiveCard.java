package it.polimi.ingsw.gc07.model.cards;

import it.polimi.ingsw.gc07.model.enumerations.CardType;

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
     * @param cardType : type of the card
     * @param scoringCondition : scoring condition of the card
     * @param objectiveScore : points that can be obtained
     */
    public ObjectiveCard(int cardID, CardType cardType, Condition scoringCondition, int objectiveScore) {
        super(cardID, cardType);
        this.scoringCondition = scoringCondition;
        this.objectiveScore = objectiveScore;
    }

    /**
     * Getter method of the attribute scoringCondition
     * @return this.scoringCondition
     */
    public Condition getScoringCondition(){
        return this.scoringCondition;
    };

    /**
     * Getter method of the attribute objectiveScore
     * @return this.objectiveScore
     */
    public int getScore(){
        return this.objectiveScore;
    };
}

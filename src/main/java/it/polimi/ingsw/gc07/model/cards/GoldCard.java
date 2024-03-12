package it.polimi.ingsw.gc07.model.cards;

import it.polimi.ingsw.gc07.model.enumerations.CardType;
import it.polimi.ingsw.gc07.model.GameItem;
import it.polimi.ingsw.gc07.model.enumerations.GameResource;

import java.util.concurrent.locks.Condition;

/**
 * Class that represents gold cards.
 */
public class GoldCard extends NonStarterCard {
    /**
     * Attribute that shows the placement condition of the gold card
     */
    private final Condition placementCondition;

    private final boolean hasScoringCondition;
    /**
     * Attribute that shows the scoring condition of the gold card
     */
    private final Condition scoringCondition;

    /**
     * Constructor of the gold card class
     * @param cardID : id of the card
     * @param cardType : type of the card
     * @param frontCorners : corners that the front of the card has
     * @param frontCornersContent : game items that the front of the card has
     * @param placementScore : scorable points
     * @param permanentResource : permanent game items that the back of the card has
     * @param placementCondition : placement condition
     * @param scoringCondition : scoring condition
     */
    public GoldCard(int cardID, CardType cardType, boolean[] frontCorners,
                    GameItem[] frontCornersContent, int placementScore,
                    GameResource permanentResource, Condition placementCondition,
                    boolean hasScoringCondition, Condition scoringCondition) {
        super(cardID, cardType, frontCorners, frontCornersContent, placementScore, permanentResource);
        this.placementCondition = placementCondition;
        this.hasScoringCondition = hasScoringCondition;
        this.scoringCondition = scoringCondition;
    }

    /**
     * Getter method of the attribute placementCondition
     * @return this.placementCondition
     */
    public Condition getPlacementCondition() {
        return this.placementCondition;
    };

    public boolean hasScoringCondition(){
        return this.hasScoringCondition;
    }

    /**
     * Getter method of the attribute scoringCondition
     * @return this.scoringCondition
     */
    public Condition getScoringCondition() {
        return this.scoringCondition;
    };
}

package it.polimi.ingsw.gc07.updates;

import it.polimi.ingsw.gc07.model.cards.DrawableCard;
import it.polimi.ingsw.gc07.model.cards.GoldCard;
import it.polimi.ingsw.gc07.model.cards.ObjectiveCard;
import it.polimi.ingsw.gc07.model_view.GameView;

import java.util.List;

/**
 * Update used to show new decks' available cards.
 */
public class DeckUpdate implements Update {
    /**
     * Card on top of resource cards deck.
     */
    private final DrawableCard topResourceCard;
    /**
     * Card on top of gold cards deck.
     */
    private final GoldCard topGoldCard;
    /**
     * List of revealed resource cards.
     */
    private final List<DrawableCard> resourceFaceUpCards;
    /**
     * List of revealed gold cards.
     */
    private final List<GoldCard> goldFaceUpCards;
    /**
     * Common objective cards.
     */
    private final List<ObjectiveCard> commonObjective;

    /**
     * Constructor of DeckUpdate.
     * @param topResourceCard resource card on top
     * @param topGoldCard gold card on top
     * @param resourceFaceUpCards revealed resource cards
     * @param goldFaceUpCards revealed gold cards
     */
    public DeckUpdate(DrawableCard topResourceCard, GoldCard topGoldCard, List<DrawableCard> resourceFaceUpCards, List<GoldCard> goldFaceUpCards, List<ObjectiveCard> commonObjective) {
        this.topResourceCard = topResourceCard;
        this.topGoldCard = topGoldCard;
        this.resourceFaceUpCards = resourceFaceUpCards;
        this.goldFaceUpCards = goldFaceUpCards;
        this.commonObjective = commonObjective;
    }

    /**
     * Execute method of the concrete update: sets new values for deck view elements.
     * @param gameView game view to update
     */
    @Override
    public void execute(GameView gameView) {
        gameView.setDeck(topResourceCard, topGoldCard, resourceFaceUpCards, goldFaceUpCards, commonObjective);
    }
}

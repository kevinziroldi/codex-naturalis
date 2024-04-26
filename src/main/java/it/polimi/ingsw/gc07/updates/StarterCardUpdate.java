package it.polimi.ingsw.gc07.updates;

import it.polimi.ingsw.gc07.model.cards.PlaceableCard;
import it.polimi.ingsw.gc07.model_view.GameView;

// TODO must be used only with the owner of the game view, not with every listener
public class StarterCardUpdate implements Update {
    /**
     * Starter card.
     */
    private final PlaceableCard starterCard;

    /**
     * Constructor of StarterCardUpdate.
     * @param starterCard starter card to show
     */
    public StarterCardUpdate(PlaceableCard starterCard) {
        this.starterCard = starterCard;
    }

    /**
     * Execute method of the concrete update: reveals the starter card to the player.
     * @param gameView game view to update
     */
    @Override
    public void execute(GameView gameView) {
        gameView.setStarterCard(starterCard);
    }
}

package it.polimi.ingsw.gc07.updates;

import it.polimi.ingsw.gc07.controller.GameState;
import it.polimi.ingsw.gc07.model_view.GameView;

import java.util.List;

public class GameModelUpdate implements Update {
    /**
     * Game id.
     */
    private final int id;
    /**
     * Game state.
     */
    private final GameState state;
    /**
     * Current player.
     */
    private final int currPlayer;
    /**
     * Boolean value for twentyPointsReached.
     */
    private final boolean twentyPointsReached;
    /**
     * Boolean value for additionalRound.
     */
    private final boolean additionalRound;

    /**
     * Constructor of GameModelUpdate.
     * @param id id
     * @param state state
     * @param currPlayer current player
     */
    public GameModelUpdate(int id, GameState state, int currPlayer, boolean twentyPointsReached, boolean additionalRound) {
        this.id = id;
        this.state = state;
        this.currPlayer = currPlayer;
        this.twentyPointsReached = twentyPointsReached;
        this.additionalRound = additionalRound;
    }

    /**
     * Execute method of the concrete update: allows to notify a game model update.
     * @param gameView gameView game view to update
     */
    @Override
    public void execute(GameView gameView) {
        gameView.setGameModel(id, state, currPlayer, twentyPointsReached, additionalRound);
    }
}


package it.polimi.ingsw.gc07.controller;

import java.io.Serializable;

public abstract class GameCommand implements Serializable {
    /**
     * Game in which the command has to be executed.
     */
    private Game game;

    /**
     * Setter for attribute game.
     * @param game game
     */
     public void setGame(Game game) {
        this.game = game;
    }
    /**
     * Getter for the attribute game.
     * @return game
     */
    Game getGame() {
         return game;
    }
    /**
     * Method to execute the game command.
     */
    public abstract void execute();
}

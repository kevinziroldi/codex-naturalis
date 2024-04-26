package it.polimi.ingsw.gc07.game_commands;

import it.polimi.ingsw.gc07.controller.GameController;

import java.io.Serializable;

public interface GameCommand extends Serializable {
    /**
     * Method to execute the game command.
     * @param gameController game controller
     */
    void execute(GameController gameController);
}

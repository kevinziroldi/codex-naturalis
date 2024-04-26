package it.polimi.ingsw.gc07.game_commands;

import it.polimi.ingsw.gc07.controller.GamesManager;

import java.io.Serializable;

public interface GamesManagerCommand extends Serializable {
    /**
     * Getter method for the nickname of games manager command.
     * Used to find the VirtualView who made the request.
     * @return nickname
     */
    public abstract String getNickname();

    /**
     * Method to execute the game command.
     */
    public abstract void execute(GamesManager gamesManager);
}

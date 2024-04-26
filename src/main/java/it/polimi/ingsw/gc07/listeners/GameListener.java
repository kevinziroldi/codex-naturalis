package it.polimi.ingsw.gc07.listeners;

import it.polimi.ingsw.gc07.updates.CommandResultUpdate;
import it.polimi.ingsw.gc07.updates.GameModelUpdate;
import it.polimi.ingsw.gc07.updates.PlayerJoinedUpdate;

public interface GameListener {
    /**
     * Method used to notify a game model update.
     * @param gameModelUpdate game model update
     */
    void receiveGameModelUpdate(GameModelUpdate gameModelUpdate);

    /**
     * Method used to notify that a player has joined.
     * @param playerJoinedUpdate playerJoinedUpdate
     */
    void receivePlayerJoinedUpdate(PlayerJoinedUpdate playerJoinedUpdate);

    /**
     * Method used to notify a command result update.
     * @param commandResultUpdate command result update
     */
    void receiveCommandResultUpdate(CommandResultUpdate commandResultUpdate);
}

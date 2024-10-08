package it.polimi.ingsw.gc07.network.rmi;

import it.polimi.ingsw.gc07.game_commands.GameControllerCommand;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Virtual server of the single game.
 */
public interface VirtualServerGame extends Remote {
    /**
     * Method to set a command and execute game command.
     * @param gameControllerCommand game command to set and execute
     * @throws RemoteException remote exception
     */
    void setAndExecuteCommand(GameControllerCommand gameControllerCommand) throws RemoteException;
}

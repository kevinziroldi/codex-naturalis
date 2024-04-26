package it.polimi.ingsw.gc07.network.rmi;

import it.polimi.ingsw.gc07.controller.GameController;
import it.polimi.ingsw.gc07.game_commands.GameCommand;
import it.polimi.ingsw.gc07.network.VirtualServerGame;
import it.polimi.ingsw.gc07.network.VirtualView;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class RmiServerGame extends UnicastRemoteObject implements VirtualServerGame {
    /**
     * Game controller of the server.
     */
    private final GameController gameController;
    /**
     * Virtual views of clients in the game.
     */
    private final List<VirtualView> clients;

    public RmiServerGame(GameController gameController) throws RemoteException {
        this.gameController = gameController;
        this.clients = new ArrayList<>();
    }

    /**
     * Method that allows a client to connect with the RMI server.
     * @param client client to connect
     * @throws RemoteException remote exception
     */
    @Override
    public synchronized void connect(VirtualView client) throws RemoteException {
        clients.add(client);
        gameController.addRMIListener(client);
        System.err.println("New client connected");
    }

    /**
     * Method that allows the client to execute a game command.
     * @param gameCommand game command to set and execute
     * @throws RemoteException remote exception
     */
    @Override
    public synchronized void setAndExecuteCommand(GameCommand gameCommand) throws RemoteException {
        gameController.setAndExecuteCommand(gameCommand);

        // only for testing
        System.out.println(gameController.getCommandResult());
    }
}

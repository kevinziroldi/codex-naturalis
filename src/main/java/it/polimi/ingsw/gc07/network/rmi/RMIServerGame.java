package it.polimi.ingsw.gc07.network.rmi;

import it.polimi.ingsw.gc07.controller.Game;
import it.polimi.ingsw.gc07.controller.GameCommand;
import it.polimi.ingsw.gc07.controller.enumerations.CommandResult;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class RMIServerGame implements VirtualServerGame {
    /**
     * Game controller of the game.
     */
    private final Game game;
    /**
     * Virtual views of clients in the game.
     */
    private final List<VirtualView> clients;

    public RMIServerGame(Game game) throws RemoteException {
        this.game = game;
        this.clients = new ArrayList<>();
    }

    /**
     * Method that allows a client to connect with the RMI server.
     * @param client client to connect
     * @throws RemoteException remote exception
     */
    @Override
    public void connect(VirtualView client) throws RemoteException {
        clients.add(client);
        System.err.println("New client connected");
    }

    /**
     * Method that allows the client to execute a game command.
     * @param gameCommand game command to set and execute
     * @throws RemoteException remote exception
     */
    @Override
    public void setAndExecuteCommand(GameCommand gameCommand) throws RemoteException {
        gameCommand.setGame(game);
        game.setAndExecuteCommand(gameCommand);
        System.out.println(game.getCommandResultManager().getCommandResult());
        if(game.getCommandResultManager().getCommandResult().equals(CommandResult.SUCCESS)) {
            // TODO stampa aggiornamento al client
        }else {
            System.out.println(game.getCommandResultManager().getCommandResult().getResultMessage());
        }
    }
}

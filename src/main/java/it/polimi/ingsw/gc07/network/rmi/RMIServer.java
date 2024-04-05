package it.polimi.ingsw.gc07.network.rmi;

import it.polimi.ingsw.gc07.controller.GameCommand;
import it.polimi.ingsw.gc07.controller.GamesManager;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class RMIServer implements VirtualServer {
    final GamesManager gamesManager;
    final List<VirtualView> clients;

    public RMIServer(GamesManager gamesManager) {
        this.gamesManager = gamesManager;
        this.clients = new ArrayList<>();
    }

    @Override
    public synchronized void connect(VirtualView client) throws RemoteException {
        clients.add(client);
        System.err.println("New client connected");
    }

    @Override
    public void setAndExecuteCommand(GameCommand gameCommand) throws RemoteException {
        // TODO
    }

    public static void main(String[] args) throws RemoteException {
        String name = "VirtualServer";
        VirtualServer virtualServer = new RMIServer(new GamesManager());
        VirtualServer stub = (VirtualServer) UnicastRemoteObject.exportObject(virtualServer, 0);
        Registry registry = LocateRegistry.createRegistry(1234);
        registry.rebind(name, stub);
        System.out.println("GamesManager bound");
    }
}

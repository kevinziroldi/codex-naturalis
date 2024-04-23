package it.polimi.ingsw.gc07.main;

import it.polimi.ingsw.gc07.controller.GamesManager;
import it.polimi.ingsw.gc07.network.rmi.RmiServerGamesManager;
import it.polimi.ingsw.gc07.network.socket.SocketGamesManagerServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerMain {
    public static void main(String[] args) throws RemoteException, IOException{ //IOException sollevata da linea 33
        // create RMI server
        String name = "VirtualServerGamesManager";
        RmiServerGamesManager serverGamesManager = new RmiServerGamesManager(GamesManager.getGamesManager());
        Registry registry = LocateRegistry.createRegistry(1234);
        registry.rebind(name, serverGamesManager);
        System.out.println("GamesManager bound");

        // create Socket server for gamesManager
        //TODO per adesso la porta è data da linea di comando, stabilire se bisogna cambiarlo
        int port = Integer.parseInt(args[0]);   // TODO penso args[0]
        ServerSocket sc = null;
        try{
            sc = new ServerSocket(port);
        } catch (IOException e){
            System.out.println("Unable to start the main server: unavailable port");
            throw new RuntimeException();
        }
        System.out.println("Main server ready");
        new SocketGamesManagerServer(sc).runServer();
    }
}

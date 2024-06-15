package it.polimi.ingsw.gc07.main;

import it.polimi.ingsw.gc07.network.rmi.RmiServerGamesManager;
import it.polimi.ingsw.gc07.network.socket.SocketServer;
import it.polimi.ingsw.gc07.utils.SafePrinter;

import java.io.IOException;
import java.net.ServerSocket;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerMain {
    /**
     * Main method of the server
     */
    public static void main(String[] args) {

        // create Rmi server
        String name = "VirtualServerGamesManager";
        RmiServerGamesManager serverGamesManager = RmiServerGamesManager.getRmiServerGamesManager();
        String serverIp = args[0];
        System.setProperty("java.rmi.server.hostname", serverIp);

        System.setProperty("sun.rmi.transport.tcp.responseTimeout", "1000");
        System.setProperty("sun.rmi.transport.tcp.readTimeout", "1000");
        System.setProperty("sun.rmi.dgc.ackTimeout", "1000");
        System.setProperty("sun.rmi.transport.connectionTimeout", "1000");

        SafePrinter.println("RMI properties set: ");
        SafePrinter.println("sun.rmi.transport.tcp.responseTimeout = " + System.getProperty("sun.rmi.transport.tcp.responseTimeout"));
        SafePrinter.println("sun.rmi.transport.tcp.readTimeout = " + System.getProperty("sun.rmi.transport.tcp.readTimeout"));
        SafePrinter.println("sun.rmi.dgc.ackTimeout = " + System.getProperty("sun.rmi.dgc.ackTimeout"));
        SafePrinter.println("sun.rmi.transport.connectionTimeout = " + System.getProperty("sun.rmi.transport.connectionTimeout"));


        int rmiPort = Integer.parseInt(args[1]);
        Registry registry;
        try {
            registry = LocateRegistry.createRegistry(rmiPort);
            registry.rebind(name, serverGamesManager);
        } catch (RemoteException e) {
            System.exit(-1);
        }
        SafePrinter.println("RMI Server running");

        // create Socket server for gamesManager
        int socketPort = Integer.parseInt(args[2]);
        ServerSocket sc;
        try{
            sc = new ServerSocket(socketPort);
        } catch (IOException e){
            SafePrinter.println("Unable to start the main server: unavailable port");
            throw new RuntimeException();
        }
        SafePrinter.println("Main Socket server ready");
        SocketServer socketServer= SocketServer.getSocketServer();
        socketServer.initializeSocketServer(sc);
        socketServer.runServer();
    }
}


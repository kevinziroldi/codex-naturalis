package it.polimi.ingsw.gc07.network.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface VirtualView extends Remote {
    public void setServerGame(VirtualServerGame serverGame) throws RemoteException;
    public String getNickname() throws RemoteException;
}

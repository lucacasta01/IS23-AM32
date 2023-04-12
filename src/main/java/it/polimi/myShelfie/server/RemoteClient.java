package it.polimi.myShelfie.server;
import java.rmi.Remote;
import java.rmi.RemoteException;
public interface RemoteClient extends Remote{
    void receiveMessage(String message) throws RemoteException;
}

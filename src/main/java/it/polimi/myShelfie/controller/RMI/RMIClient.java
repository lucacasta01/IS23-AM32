package it.polimi.myShelfie.controller.RMI;
import java.rmi.Remote;
import java.rmi.RemoteException;
public interface RMIClient extends Remote{
    void receiveMessage(String message) throws RemoteException;
}

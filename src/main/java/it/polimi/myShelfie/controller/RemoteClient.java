package it.polimi.myShelfie.controller;
import java.rmi.Remote;
import java.rmi.RemoteException;
public interface RemoteClient extends Remote{
    void receiveMessage(String message) throws RemoteException;
}

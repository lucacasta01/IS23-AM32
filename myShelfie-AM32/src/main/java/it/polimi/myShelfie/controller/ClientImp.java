package it.polimi.myShelfie.controller;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.server.*;
import java.util.Locale;

public class ClientImp extends UnicastRemoteObject implements RemoteClient{
    private ConnectionThread connectionThread;

    public ClientImp(ConnectionThread connectionThread) throws RemoteException{
        this.connectionThread = connectionThread;
    }

    public void receiveMessage(String message) throws RemoteException{
        connectionThread.sendMessage(message);
    }
}

package it.polimi.myShelfie.controller.RMI;

import it.polimi.myShelfie.model.Position;
import it.polimi.myShelfie.utilities.beans.Response;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface RMIServer extends Remote {
    void chatMessage(String username, String message) throws RemoteException;
    void pickTiles(String username, List<Position> chosenTiles)  throws RemoteException;
    void selectColumn(String username, Integer column) throws RemoteException;
    void order(String username, String newOrder) throws RemoteException;
    void lobbyKill(String username) throws RemoteException;
    void help(String username) throws RemoteException;
    void infoMessage(String username, String message) throws RemoteException;
    void quit(String username) throws RemoteException;
    void login(RMIClient client) throws RemoteException;

}
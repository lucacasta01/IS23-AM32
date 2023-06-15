package it.polimi.myShelfie.network.RMI;
import it.polimi.myShelfie.utilities.Position;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface RMIServer extends Remote {
    void chatMessage(String username, String message) throws RemoteException;
    void privateMessage(String username, String message) throws RemoteException;
    void pickTiles(String username, List<Position> chosenTiles)  throws RemoteException;
    void selectColumn(String username, Integer column) throws RemoteException;
    void order(String username, String newOrder) throws RemoteException;
    void lobbyKill(String username) throws RemoteException;
    void help(String username) throws RemoteException;
    void infoMessage(String username, String message) throws RemoteException;
    void quit(String username) throws RemoteException;
    void addClient(RMIClient client) throws RemoteException;
    boolean login(String username, RMIClient client) throws RemoteException;
    void ping() throws RemoteException;

    void requestMenu(String username) throws RemoteException;
}

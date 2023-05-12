package it.polimi.myShelfie.controller.RMI;
import it.polimi.myShelfie.utilities.beans.View;
import java.rmi.Remote;
import java.rmi.RemoteException;
public interface RMIClient extends Remote{
     void nicknameAccepted() throws RemoteException;
     void update(View view) throws RemoteException;
     void chatMessage(String sender, String message) throws RemoteException;
     void valid(String message) throws RemoteException;
     void denied(String message) throws RemoteException;
     void infoMessage(String message) throws RemoteException;
     void remoteShutdown(String message) throws RemoteException;
     void notifyGameJoined()throws RemoteException;
     void ping() throws RemoteException;
}

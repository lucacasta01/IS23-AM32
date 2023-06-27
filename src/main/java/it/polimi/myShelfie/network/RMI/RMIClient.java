package it.polimi.myShelfie.network.RMI;
import it.polimi.myShelfie.utilities.pojo.ChatMessage;
import it.polimi.myShelfie.view.View;
import java.rmi.Remote;
import java.rmi.RemoteException;
public interface RMIClient extends Remote{
     void nicknameAccepted() throws RemoteException;
     void nicknameDenied(String message) throws RemoteException;
     void update(View view) throws RemoteException;
     void chatMessage(ChatMessage chatMessage) throws RemoteException;
     void valid(String message) throws RemoteException;
     void denied(String message) throws RemoteException;
     void infoMessage(String message) throws RemoteException;
     void remoteShutdown(String message) throws RemoteException;
     void notifyLobbyCreated(String lobbySize)throws RemoteException;
     void notifyGameJoined()throws RemoteException;
     void ping() throws RemoteException;
     void notifyGameStarted() throws RemoteException;
     void denyLoadGame() throws RemoteException;
     void acceptLoadGame() throws RemoteException;
     void notifyNewJoin() throws RemoteException;
     void returnToMenu() throws RemoteException;
     void foundOldGame() throws RemoteException;
     void oldGameNotFound() throws  RemoteException;
     void denyRandomGame() throws RemoteException;
     void acceptCollect() throws RemoteException;
     void notifyGameEnded(String rank) throws RemoteException;
}

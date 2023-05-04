package it.polimi.myShelfie.controller.RMI;

import it.polimi.myShelfie.application.Server;
import it.polimi.myShelfie.controller.ClientHandler;
import it.polimi.myShelfie.model.Position;
import it.polimi.myShelfie.utilities.Constants;
import it.polimi.myShelfie.utilities.beans.Action;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class RMIController extends UnicastRemoteObject implements RMIServer,Runnable{
    private final List<RMIClient> rmiClients;
    private final Server server;

    public RMIController() throws RemoteException {
        rmiClients = new ArrayList<>();
        server = Server.getInstance();
    }

    @Override
    public void run() {
        try {
            startServer();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void startServer() throws RemoteException {
        Registry registry = LocateRegistry.createRegistry(Constants.RMIPORT);
        try{
            registry.bind(Constants.RMINAME, this);
        }catch(Exception e){
            e.printStackTrace();
        }
        System.out.println("RMI server on");
    }

    @Override
    public void chatMessage(String username, String message) throws RemoteException {
        ClientHandler ch = server.getConnectedClients()
                .keySet()
                .stream()
                .filter(c -> c.getNickname().equals(username))
                .toList().get(0);
        ch.setRmiAction(new Action(Action.ActionType.CHAT,username,message,null,null,null));
    }

    @Override
    public void pickTiles(String username, List<Position> chosenTiles) throws RemoteException {
        ClientHandler ch = server.getConnectedClients()
                .keySet()
                .stream()
                .filter(c -> c.getNickname().equals(username))
                .toList().get(0);

        ch.setRmiAction(new Action(Action.ActionType.PICKTILES,username,null,null,chosenTiles,null));
    }

    @Override
    public void selectColumn(String username, Integer column) throws RemoteException {
        ClientHandler ch = server.getConnectedClients()
                .keySet()
                .stream()
                .filter(c -> c.getNickname().equals(username))
                .toList().get(0);

        ch.setRmiAction(new Action(Action.ActionType.SELECTCOLUMN,username,null,null,null,column));
    }

    @Override
    public void order(String username, String newOrder) throws RemoteException {
        ClientHandler ch = server.getConnectedClients()
                .keySet()
                .stream()
                .filter(c -> c.getNickname().equals(username))
                .toList().get(0);

        ch.setRmiAction(new Action(Action.ActionType.ORDER,username,null,newOrder,null,null));
    }

    @Override
    public void lobbyKill(String username) throws RemoteException {
        ClientHandler ch = server.getConnectedClients()
                .keySet()
                .stream()
                .filter(c -> c.getNickname().equals(username))
                .toList().get(0);

        ch.setRmiAction(new Action(Action.ActionType.LOBBYKILL,username,null,null,null,null));
    }

    @Override
    public void help(String username) throws RemoteException {
        ClientHandler ch = server.getConnectedClients()
                .keySet()
                .stream()
                .filter(c -> c.getNickname().equals(username))
                .toList().get(0);

        ch.setRmiAction(new Action(Action.ActionType.HELP,username,null,null,null,null));
    }

    @Override
    public void infoMessage(String username, String message) throws RemoteException {
        ClientHandler ch = server.getConnectedClients()
                .keySet()
                .stream()
                .filter(c -> c.getNickname().equals(username))
                .toList().get(0);

        ch.setRmiAction(new Action(Action.ActionType.INFO,username,null,message,null,null));
    }

    @Override
    public void quit(String username) throws RemoteException {
        ClientHandler ch = server.getConnectedClients()
                .keySet()
                .stream()
                .filter(c -> c.getNickname().equals(username))
                .toList().get(0);

        ch.setRmiAction(new Action(Action.ActionType.QUIT,username,null,null,null,null));
    }

    @Override
    public void login(RMIClient client) throws RemoteException {
        ClientHandler clientHandler = new ClientHandler();
        clientHandler.setRmiClient(client);
        server.addClientHandler(clientHandler);
    }
}
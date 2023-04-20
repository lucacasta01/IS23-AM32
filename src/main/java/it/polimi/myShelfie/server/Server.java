package it.polimi.myShelfie.server;
import it.polimi.myShelfie.utilities.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.rmi.Remote;
import java.util.*;
import java.rmi.registry.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Server implements Runnable{

    boolean done = false;
    private static Server instance;
    private ServerSocket serverSocket;
    private Registry registry;
    private ExecutorService pool;
    private List<ClientHandler> connectedClients;
    private Map<String, String> userGame;
    private List <Lobby> lobbyList;


    private Server(){
        try {
            connectedClients = new ArrayList<>();
            userGame = new HashMap<>();
            lobbyList = new ArrayList<>();
            serverSocket = new ServerSocket(Constants.PORT);
            registry = LocateRegistry.createRegistry(Constants.PORT+1);
        }
        catch(Exception e){
            System.err.println("Server side exception thrown: " + e.toString());
            e.printStackTrace();
        }
    }

    public void removeClient(ClientHandler client){
        if(connectedClients.contains(client)){
            connectedClients.remove(client);
        }
        else{
            System.out.println("Client not connected");
        }
    }

    public Map<String, String> getUserGame() {
        return userGame;
    }

    public List<Lobby> getLobbyList() {
        return lobbyList;
    }


    public static synchronized Server getInstance(){
        if(instance == null){
            instance = new Server();
        }
        return instance;
    }

    private void shutdown(){
        try{
            done = true;
            if(!instance.serverSocket.isClosed()){
                serverSocket.close();
            }
            for(ClientHandler t : connectedClients){
                t.shutdown();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void run(){
        System.out.println("Server started");
        pool = Executors.newCachedThreadPool();
        while(!done){
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client Accepted, number of connected hosts: " + (connectedClients.size()+1));
                ClientHandler clientHandler = new ClientHandler(clientSocket, registry);
                connectedClients.add(clientHandler);
                pool.execute(clientHandler);
            }
            catch(Exception e){
                System.err.println("Server side error" +  e.toString());
                e.printStackTrace();
            }
        }
    }

    public void broadcastMessage(String message){
        for(ClientHandler t : connectedClients){
            t.sendInfoMessage(message);
        }
    }

    public static void main(String[] args){
        Server server = Server.getInstance();
        server.run();
    }


}

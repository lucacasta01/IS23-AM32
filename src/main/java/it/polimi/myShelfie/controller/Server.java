package it.polimi.myShelfie.controller;
import java.io.IOException;
import java.net.*;
import java.util.*;
import java.rmi.registry.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Server implements Runnable{

    boolean done = false;
    private static Server instance;
    private static final int PORT = 1234;
    private ServerSocket serverSocket;
    private Registry registry;
    private ExecutorService pool;

    private List<ClientHandler> connectedClients;

    private Server(){
        try {
            connectedClients = new ArrayList<>();
            serverSocket = new ServerSocket(PORT);
            registry = LocateRegistry.createRegistry(PORT+1);
        }
        catch(Exception e){
            System.err.println("Server side exception thrown: " + e.toString());
            e.printStackTrace();
        }
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
            t.sendMessage(message);
        }
    }

    public static void main(String[] args){
        Server server = Server.getInstance();
        server.run();
    }
}

package it.polimi.myShelfie.controller;
import java.net.*;
import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.util.Locale;

public class Server {
    private static Server instance;
    private static final int PORT = 1234;
    private ServerSocket serverSocket;
    private Registry registry;

    private Server(){
        try {
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

    public void start(){
        while(true){
            try {
                Socket clientSocket = serverSocket.accept();
                ConnectionThread connectionThread = new ConnectionThread(clientSocket, registry);
                connectionThread.start();
            }
            catch(Exception e){
                System.err.println("Server side error" +  e.toString());
                e.printStackTrace();
            }
        }
    }
}

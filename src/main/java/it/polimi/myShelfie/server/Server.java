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

    private Server(){
        try {
            connectedClients = new ArrayList<>();
            serverSocket = new ServerSocket(Constants.PORT);
            registry = LocateRegistry.createRegistry(Constants.PORT+1);
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

        public class ClientHandler implements Runnable {
            private Socket clientSocket;
            private String nickname;
            private Registry registry;
            private boolean isRMI = false;
            private Remote client;
            private BufferedReader in;
            private PrintWriter out;


            public ClientHandler(Socket clientSocket, Registry registry) {
                this.clientSocket = clientSocket;
                this.registry = registry;
            }

            public void shutdown() {
                try {
                    in.close();
                    out.close();
                    if (!clientSocket.isClosed()) {
                        clientSocket.close();
                    }
                } catch (Exception e) {
                    System.out.println(e.toString());
                }
            }

            public void run() {
                try {
                    in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    out = new PrintWriter(clientSocket.getOutputStream(), true);
                } catch (Exception e) {
                    System.err.println("Exception throws during stream creation: " + e.toString());
                    e.printStackTrace();
                }
                try {
                    out.println("Please insert your username");
                    nickname = in.readLine();
                    System.out.println(nickname + " connected to the server");
                    Server myServer = Server.getInstance();
                    myServer.broadcastMessage(nickname + " joined");
                    String message;
                    while (in != null && (message = in.readLine()) != null) {
                        if (message.startsWith("/quit")) {
                            connectedClients.remove(this);
                            shutdown();
                            System.out.println(nickname+" disconnected");
                            myServer.broadcastMessage(nickname+" disconnected");
                            break;
                        } else {
                            Server.getInstance().broadcastMessage(nickname + ": " + message);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Exception throws while handling connection");
                    e.printStackTrace();
                }
            }

        /*
        public void setRMI(boolean isRMI){
            this.isRMI = isRMI;
            if(isRMI){
                try{
                    client = new ClientImp(this);
                    registry.rebind("client", client);
                }
                catch(Exception e){
                    System.err.println("Error during client rebinding: " + e.toString());
                    e.printStackTrace();
                }
            }
            else {
                try{
                    registry.unbind("client");
                }
                catch(Exception e){
                    System.err.println("Error during client unbinding: "+ e.toString());
                }
            }
        }

        private void handleTCPCom(Object input){
            if(input instanceof String){
                String message = (String) input;
                if(message.equalsIgnoreCase("switch to rmi")) {
                    sendMessage("Switching to RMI communication");
                    setRMI(true);
                }
                else {sendMessage("Received message via TCP "+ message);}
            }
            else {
                sendMessage("Invalid message received via TCP");
            }
        }

        private void handleRMICom(Object input){
            if(input instanceof String){
                String message = (String) input;
                if(message.equalsIgnoreCase("switch to tcp")){
                    sendMessage("Switching to TCP communication");
                    setRMI(false);
                }
                else {
                    sendMessage("Received message via RMI" +  message);
                }
            }
            else {
                sendMessage("Invalid message received via RMI");
            }
        }
        */

            public synchronized void sendMessage(String message) {
                try {
                    out.println(message);
                } catch (Exception e) {
                    System.out.println("Error occurred while sending a message: " + e.toString());
                    e.printStackTrace();
                }
            }
        }
}

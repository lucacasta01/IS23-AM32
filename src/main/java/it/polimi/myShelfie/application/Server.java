package it.polimi.myShelfie.application;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.polimi.myShelfie.controller.RMI.RMIController;
import it.polimi.myShelfie.controller.ping.ServerPingThread;
import it.polimi.myShelfie.controller.ClientHandler;
import it.polimi.myShelfie.controller.Lobby;
import it.polimi.myShelfie.controller.ping.ServerRmiPingThread;
import it.polimi.myShelfie.controller.ping.ServerTcpPingThread;
import it.polimi.myShelfie.utilities.Settings;
import it.polimi.myShelfie.utilities.JsonParser;
import it.polimi.myShelfie.utilities.beans.Action;
import it.polimi.myShelfie.utilities.beans.Usergame;
import java.io.*;
import java.net.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.rmi.registry.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Server extends UnicastRemoteObject implements Runnable{

    boolean done = false;
    private static Server instance;
    private ServerSocket serverSocket;
    private Registry registry;
    private ExecutorService pool;
    private ExecutorService lobbyPool;
    private ExecutorService pingPool;
    private final Map<ClientHandler, ServerPingThread> connectedClients;
    private Map<String, String> userGame;
    private final List<Lobby> lobbyList;
    public boolean acceptOn = true;

    private Server() throws RemoteException {
        connectedClients = new HashMap<>();
        lobbyList = new ArrayList<>();
        try {
            serverSocket = new ServerSocket(Settings.TCPPORT);
            userGame = loadUserGame();
        }
        catch(Exception e){
            System.err.println("Server side exception thrown: " + e.toString());
            e.printStackTrace();
        }
    }

    /**
     *get instance method for singleton pattern
     * @return instance if not null, a new server instance if null
     */
    public static synchronized Server getInstance() {
        if(instance == null){
            try {
                instance = new Server();
            }catch(RemoteException e){
                e.printStackTrace();
                //todo
            }
        }
        return instance;
    }

    /**
     * removes a client from the server's connected clients map
     * @param client
     */
    public void removeClient(ClientHandler client){
        if(connectedClients.containsKey(client)){
            connectedClients.remove(client);
        }
        else{
            System.out.println("Client not connected");
        }
    }

    /**
     * @return userGame map([nickname->old game uid])
     */
    public Map<String, String> getUserGame() {
        return userGame;
    }

    /**
     *
     * @return a list of active lobbys
     */
    public synchronized List<Lobby> getLobbyList() {
        return lobbyList;
    }

    public void addRmiClientHandler(ClientHandler ch){
        synchronized (this.connectedClients) {
            this.connectedClients.put(ch, new ServerRmiPingThread(ch));
            pool.execute(ch);
            pingPool.execute(connectedClients.get(ch));
        }
    }

    /**
     * turns the server off
     */
    public void shutdown(){
        try{
            done = true;
            if(!instance.serverSocket.isClosed()){
                serverSocket.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run(){
        System.out.println("Server started");
        pool = Executors.newCachedThreadPool();
        lobbyPool = Executors.newCachedThreadPool();
        pingPool = Executors.newCachedThreadPool();


        //Start RMI server
        try {
            new Thread(new RMIController()).start();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }

        //Start tcp accepter
        new TCPaccepter().start();

        //Start server inputHandler
        new ServerInputHandler().start();
    }

    /**
     * saves the usergame map to a json file
     */
    public void saveUserGame(){
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        try {
            if(getClass().getResource("/config/usergame.json") == null){
                System.out.println("Creating new file, path: "+getClass().getResource("/config").getPath()+"/usergame.json");
               new File(getClass().getResource("/config").getPath()+"/usergame.json").createNewFile();
            }
            FileWriter fw = new FileWriter(getClass().getResource("/config/usergame.json").getPath().replace("file:/", ""));
            Map<String,String> toSave = new HashMap<>();
            Usergame usergame = new Usergame();
            for(String k : userGame.keySet()){
                if(!userGame.get(k).equals("-")){
                    usergame.getNicknames().add(k);
                    usergame.getUIDs().add(userGame.get(k));
                }
            }

            fw.write(gson.toJson(usergame));
            fw.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * loads the usergame map from json file
     * @return old usergame map if present, new map otherwise
     */
    public Map<String,String> loadUserGame(){
        URL url = getClass().getResource("/config/usergame.json");
        if(url == null){
            return new HashMap<>();
        }
        else{
            return JsonParser.getUsergame(url.getPath());
        }
    }

    /**
     * kills a lobby searched by UID
     * @param UID to be search
     */
    public void killLobby(String UID) {
        synchronized (lobbyList) {
            Iterator<Lobby> iter = lobbyList.iterator();
            while (iter.hasNext()) {
                Lobby l = iter.next();
                if (l.getLobbyUID().equals(UID)) {
                    synchronized (l.actions) {
                        l.actions.add(new Action(Action.ActionType.LOBBYKILL, "server", null, null, null, null));
                        l.actions.notifyAll();
                    }
                    iter.remove();
                }
            }
        }
    }

    /**
     * search if the client is in a lobby
     * @param ch
     * @return the lobby if found, null otherwise
     */
    public Lobby lobbyOf(ClientHandler ch){
        for(Lobby l : lobbyList){
            if(l.getLobbyPlayers().contains(ch)){
                return l;
            }
        }
        return null;
    }

    /**
     *
     * @return connected clients map
     */
    public Map<ClientHandler,ServerPingThread> getConnectedClients() {
        return connectedClients;
    }

    /**
     * execute the client handler ch
     * @param ch
     */
    public void executeClientHandler(ClientHandler ch){
        pool.execute(ch);
        if(Settings.pingOn) {
            pingPool.execute(connectedClients.get(ch));
        }
    }

    /**
     *
     * @param nickname
     * @return true if the client "nickname" is connected to the server
     */
    public boolean isConnected(String nickname){
        int count = 0;
        for(ClientHandler ch : connectedClients.keySet()){
            if(ch.getNickname()!=null) {
                if (ch.getNickname().equals(nickname)) {
                    count++;
                }
            }
        }
        return count>0;
    }

    /**
     *
     * @return server tcp socket
     */
    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    /**
     * execute a lobby
     * @param lobby
     */
    public synchronized void runLobby(Lobby lobby){
        lobbyPool.execute(lobby);
    }

    public static void main(String[] args){
        Server server = Server.getInstance();
        server.run();
    }
}
class TCPaccepter extends Thread {
    @Override
    public void run() {
        Server server = Server.getInstance();
        ServerSocket serverSocket = server.getServerSocket();
        while (!server.done) {
            try {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler;
                synchronized (server.getConnectedClients()) {
                    System.out.println("Client Accepted, number of connected hosts: " + (server.getConnectedClients().size() + 1));
                    clientHandler = new ClientHandler(clientSocket);
                    ServerPingThread pingThread = new ServerTcpPingThread(clientHandler);
                    server.getConnectedClients().put(clientHandler, pingThread);
                }
                server.executeClientHandler(clientHandler);
            } catch (SocketException s) {
                System.out.println("\n"+s.getMessage());
                if(s.getMessage().equals("Socket closed")){
                    System.out.println("Socket is closed, quitting TCP accepter");
                }else{
                    s.printStackTrace();
                }
            }catch (Exception e){
                System.err.println("Server side error " + e.toString());
                e.printStackTrace();
            }
        }
        System.exit(12);
    }
}

class ServerInputHandler extends Thread {
    @Override
    public void run() {
        Server server = Server.getInstance();

        String message;
        BufferedReader inReader = new BufferedReader(new InputStreamReader(System.in));
        while (!server.done) {
            try {
                message = inReader.readLine();

                if (message.equals("/q")) {
                    System.out.println("Quitting server...");
                    //closes all the lobbys
                    List<Lobby> lobbys;
                    synchronized (server.getLobbyList()){
                        lobbys = new ArrayList<>(server.getLobbyList());
                    }
                    for(Lobby l: lobbys){
                        server.killLobby(l.getLobbyUID());
                    }
                    System.out.println("All the lobbys are killed");
                    //closes all the clients
                    Map<ClientHandler, ServerPingThread> clients;
                    synchronized (server.getConnectedClients()){
                        clients = new HashMap<>(server.getConnectedClients());
                    }
                    for(ClientHandler ch:clients.keySet()){
                        ch.sendShutdown();
                    }
                    System.out.println("All the client sare killed");
                    try{
                        server.shutdown();
                    }catch (Exception e){
                        System.out.println("Error in server shutdown");
                        e.printStackTrace();
                    }

                } else if(message.equals("/h")){
                    StringBuilder help = new StringBuilder();
                    help.append("***MY SHELFIE SERVER***\n");
                    help.append(server.getConnectedClients().size()+" Connected clients\n");
                    help.append(server.getLobbyList().size()+" started games\n");
                    help.append("type /q to exit from the server app\n");
                    System.out.println(help.toString());
                }else{
                    System.out.println("*Wrong input message*");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }
}
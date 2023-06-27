package it.polimi.myShelfie.application;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.polimi.myShelfie.network.RMI.RMIController;
import it.polimi.myShelfie.network.ping.ServerPingThread;
import it.polimi.myShelfie.network.ClientHandler;
import it.polimi.myShelfie.network.Lobby;
import it.polimi.myShelfie.network.ping.ServerRmiPingThread;
import it.polimi.myShelfie.network.ping.ServerTcpPingThread;
import it.polimi.myShelfie.utilities.Settings;
import it.polimi.myShelfie.utilities.JsonParser;
import it.polimi.myShelfie.utilities.pojo.Action;
import it.polimi.myShelfie.utilities.pojo.Ports;
import it.polimi.myShelfie.utilities.pojo.Usergame;
import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Server extends UnicastRemoteObject implements Runnable{

    boolean done = false;
    private static Server instance;
    private ServerSocket serverSocket;
    private ExecutorService pool;
    private ExecutorService lobbyPool;
    private ExecutorService pingPool;
    private final Map<ClientHandler, ServerPingThread> connectedClients;
    private Map<String, String> userGame;
    private final List<Lobby> lobbyList;
    private int TCPport = 0;
    private int RMIport = 0;

    private Server() throws RemoteException {
        connectedClients = new HashMap<>();
        lobbyList = new ArrayList<>();
        try {
            Files.createDirectories(Paths.get(System.getProperty("user.dir") + "/config/savedgames/"));
        }catch (IOException e){
            System.err.println("Server side exception: failed to create directory");
            e.printStackTrace();
        }

        //load ports from json
        List<Integer> ports = JsonParser.getPortsConfig("/config/ports.json");
        if(ports!=null){
            if(ports.get(0)!=0){
                TCPport = ports.get(0);
            }else{
                TCPport = Settings.TCPPORT;
            }

            if(ports.get(1)!=0){
                RMIport = ports.get(1);
                if(RMIport == TCPport){
                    RMIport++;
                    System.out.println("RMI and TCP ports are equals. Switching RMI port to: "+ RMIport);
                }
            }else{
                RMIport = Settings.RMIPORT;
            }
        }else{
            initPortsConfig();
            TCPport = Settings.TCPPORT;
            RMIport = Settings.RMIPORT;
        }
        System.out.println("Server TCP port: "+TCPport);
        System.out.println("Server RMI port: "+RMIport);


        try {
            serverSocket = new ServerSocket(TCPport);
        }
        catch(Exception e){
            System.err.println("Server side socket exception thrown: ");
            e.printStackTrace();
        }
        try {
            userGame = loadUserGame();
        }catch (Exception e){
            System.err.println("Server side error while loading usergame");
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

    public int getRMIport(){
        return this.RMIport;
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

    private void initPortsConfig(){
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        try {
            Path path = Paths.get(System.getProperty("user.dir")+ "/config/ports.json");
            if(path.toFile().isFile()){
                new File(path.toString()).createNewFile();
            }
            FileWriter fw = new FileWriter(path.toString());
            Ports ports = new Ports();
            ports.getPorts().add(0);
            ports.getPorts().add(0);
            fw.write(gson.toJson(ports));
            fw.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * saves the usergame map to a json file
     */
    public void saveUserGame(){
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        try {
            Path usergamePath = Paths.get(System.getProperty("user.dir")+ "/config/usergame.json");
            if(usergamePath.toFile().isFile()){
               new File(usergamePath.toString()).createNewFile();
            }
            FileWriter fw = new FileWriter(usergamePath.toString());
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
        Path usergamePath = Paths.get(System.getProperty("user.dir")+ "/config/usergame.json");
        if(!usergamePath.toFile().isFile()){
            return new HashMap<>();
        }
        else{
            return JsonParser.getUsergame(usergamePath.toString());
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
                System.err.println("Server side error ");
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
                    System.out.println("All the clients are killed");
                    try{
                        server.shutdown();
                    }catch (Exception e){
                        System.out.println("Error in server shutdown");
                        e.printStackTrace();
                    }

                } else if(message.equals("/h")){
                    String help = "***MY SHELFIE SERVER***\n" +
                            server.getConnectedClients().size() + " Connected clients\n" +
                            server.getLobbyList().size() + " started games\n" +
                            "type /q to exit from the server app\n";
                    System.out.println(help);
                }else{
                    System.out.println("*Wrong input message*");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }
}
package it.polimi.myShelfie.application;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.polimi.myShelfie.controller.ClientHandler;
import it.polimi.myShelfie.controller.Lobby;
import it.polimi.myShelfie.utilities.Constants;
import it.polimi.myShelfie.utilities.JsonParser;
import it.polimi.myShelfie.utilities.PingObject;
import it.polimi.myShelfie.utilities.beans.Action;
import it.polimi.myShelfie.utilities.beans.Usergame;

import java.io.*;
import java.net.*;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    private ExecutorService lobbyPool;
    private ExecutorService pingPool;
    private Map<ClientHandler,Thread> connectedClients;
    private Map<String, String> userGame;
    private List <Lobby> lobbyList;

    private Server(){
        try {
            connectedClients = new HashMap<>();
            lobbyList = new ArrayList<>();
            serverSocket = new ServerSocket(Constants.PORT);
            registry = LocateRegistry.createRegistry(Constants.PORT+1);
            userGame = loadUserGame();
        }
        catch(Exception e){
            System.err.println("Server side exception thrown: " + e.toString());
            e.printStackTrace();
        }
    }

    public void removeClient(ClientHandler client){
        if(connectedClients.containsKey(client)){
            connectedClients.remove(client);
        }
        else{
            System.out.println("Client not connected");
        }
    }

    public Map<String, String> getUserGame() {
        return userGame;
    }

    public synchronized List<Lobby> getLobbyList() {
        return lobbyList;
    }


    public static synchronized Server getInstance(){
        if(instance == null){
            instance = new Server();
        }
        return instance;
    }
    public void executePingThread(ClientHandler ch){
        try{
            pingPool.execute(connectedClients.get(ch));
        }catch(Exception e){
            System.out.println("Error while adding ping thread");
            e.printStackTrace();
        }
    }

    private void shutdown(){
        try{
            done = true;
            if(!instance.serverSocket.isClosed()){
                serverSocket.close();
            }
            for(ClientHandler t : connectedClients.keySet()){
                t.shutdown();
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
        while(!done){
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client Accepted, number of connected hosts: " + (connectedClients.size()+1));
                ClientHandler clientHandler = new ClientHandler(clientSocket, registry);
                Thread pingThread = pingThread(clientHandler);
                connectedClients.put(clientHandler,pingThread);
                pool.execute(clientHandler);
            }
            catch(Exception e){
                System.err.println("Server side error " +  e.toString());
                e.printStackTrace();
            }
        }
    }

    public void broadcastMessage(String message){
        for(ClientHandler t : connectedClients.keySet()){
            t.sendInfoMessage(message);
        }
    }

    public void saveUserGame(){
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        try {
            FileWriter fw = new FileWriter("src/config/usergame.json");
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
            System.out.println(e.toString());
        }
    }
    public Map<String,String> loadUserGame(){
        Path path = Paths.get("src/config/usergame.json");
        if(!path.toFile().isFile()){
            return new HashMap<>();
        }
        else{
            return JsonParser.getUsergame(path.toString());
        }
    }

    public void killLobby(String UID){
        synchronized (lobbyList){
            Iterator<Lobby> iter =lobbyList.iterator();
            while(iter.hasNext()){
                Lobby l = iter.next();
                if(l.getLobbyUID().equals(UID)){

                    synchronized (l.actions){
                        l.actions.add(new Action(Action.ActionType.LOBBYKILL, "server", null, null , null , null ));
                        l.actions.notifyAll();
                    }
                    iter.remove();
                }
            }
        }
    }

    public Lobby lobbyOf(ClientHandler ch){
        for(Lobby l : lobbyList){
            if(l.getLobbyPlayers().contains(ch)){
                return l;
            }
        }
        return null;
    }

    public synchronized Map<ClientHandler,Thread> getConnectedClients() {
        return connectedClients;
    }

    public boolean isConnected(String nickname){
        int count = 0;
        for(ClientHandler ch : connectedClients.keySet()){
            if(ch.getNickname().equals(nickname)){
                count++;
            }
        }

        return count>1;
    }
    public synchronized void runLobby(Lobby lobby){
        lobbyPool.execute(lobby);
    }





    public Thread pingThread(ClientHandler ch) {

        return new Thread(() -> {
            boolean elapsed = false;
            while (!elapsed) {
                try {
                    ch.sendPing();
                } catch (IOException e) {
                    System.exit(10);
                }



                SwapElapsed swapElapsed = new SwapElapsed(ch);
                swapElapsed.start();


                while (ch.getPongResponses().size() == 0) {
                    synchronized (ch.getPongResponses()) {
                        try {
                            ch.getPongResponses().wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }


                if (ch.getPongResponses().get(0).isElapsed()) {
                    System.err.println("Ping failed for "+ch.getNickname()+"");

                    if(this.getUserGame()!=null){
                        if(this.getUserGame().get(ch.getNickname())!=null) {
                            if (this.getUserGame().get(ch.getNickname()).equals("-")) {
                                this.getUserGame().remove(ch.getNickname());
                            }
                        }
                    }
                    synchronized (this.getUserGame()){
                        this.saveUserGame();
                    }
                    if(ch.isPlaying()){
                        this.killLobby(this.lobbyOf(ch).getLobbyUID());
                        ch.shutdown();
                        this.removeClient(ch);
                    }
                    else{
                        ch.shutdown();
                        this.removeClient(ch);
                    }
                    elapsed = true;
                } else {
                    try {
                        swapElapsed.setRunning(false);
                    } catch (Exception e) {
                        //ignore
                    }
                    synchronized (ch.getPongResponses()) {
                        ch.getPongResponses().remove(0);
                    }
                    try {
                        Thread.sleep(Constants.PINGPERIOD);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                }

            }
        });
    }


    class SwapElapsed extends Thread {

        private boolean isRunning = true;
        private ClientHandler client;
        public SwapElapsed(ClientHandler ch){
            this.client = ch;
        }

        public boolean isRunning() {
            return isRunning;
        }

        public void setRunning(boolean running) {
            isRunning = running;
        }

        @Override
        public void run() {
            int time = 0;
            while (isRunning() && time < Constants.PINGTHRESHOLD) {
                try {
                    Thread.sleep(Constants.PINGTHRESHOLD/Constants.PINGFACTOR);
                    time += Constants.PINGTHRESHOLD/Constants.PINGFACTOR;
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            if (isRunning()) {
                synchronized (client.getPongResponses()) {
                    client.getPongResponses().add(new PingObject(true));
                    client.getPongResponses().notifyAll();
                }
            }
        }
    }




    public static void main(String[] args){
        Server server = Server.getInstance();
        //server.createPingThread().start();
        server.run();
    }



}


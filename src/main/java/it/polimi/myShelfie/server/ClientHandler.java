package it.polimi.myShelfie.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.polimi.myShelfie.utilities.JsonParser;
import it.polimi.myShelfie.utilities.Utils;
import it.polimi.myShelfie.utilities.beans.Action;
import it.polimi.myShelfie.utilities.beans.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.rmi.Remote;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private String nickname;
    private Registry registry;
    private boolean isRMI = false;
    private Remote client;
    private BufferedReader in;
    private PrintWriter out;
    private Server server;


    public ClientHandler(Socket clientSocket, Registry registry) {
        this.clientSocket = clientSocket;
        this.registry = registry;
        this.server = Server.getInstance();
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

    public Action getAction() throws IOException {
       return JsonParser.getAction(in.readLine());
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
            sendInfoMessage("Please insert your username");
            Action action = getAction();
            nickname = action.getInfo();
            while(action.getActionType() != Action.ActionType.INFO || server.getUserGame().containsKey(nickname)) {
                sendDeny("Nickname " + nickname + " already used, retry:");
                action = getAction();
                nickname = action.getInfo();
            }
            synchronized (server.getUserGame()){
                server.getUserGame().put(nickname,"-");
            }
            sendAccept("Username accepted");

            System.out.println(nickname + " connected to the server");
            String message;

            String chose = "1";
            boolean lobbyCreated = false;
            while(!chose.equals("0") && !lobbyCreated) {
                sendInfoMessage("(1) New Game");
                sendInfoMessage("(2) Load last game");
                sendInfoMessage("(3) Join random game");
                sendInfoMessage("(4) Search for stared saved game");
                sendInfoMessage("(0) Exit\n");
                action = getAction();
                chose = action.getInfo();
                while(action.getActionType() != Action.ActionType.INFO && !chose.equals("1") && !chose.equals("2") && !chose.equals("3") && !chose.equals("4") && !chose.equals("0")) {
                    sendDeny("Type the right key...");
                    action = getAction();
                    chose = action.getInfo();
                }
                sendAccept("Game mode selected");

                switch (chose) {
                    case "1" -> {
                        sendInfoMessage("* CREATE NEW GAME *\n\n");
                        sendInfoMessage("Enter number of players: ");
                        action = getAction();
                        message = action.getInfo();
                        while(action.getActionType() != Action.ActionType.INFO && !message.equals("2") && !message.equals("3") && !message.equals("4")) {
                            sendDeny("Type the right key...");
                            action = getAction();
                            message = action.getInfo();
                        }

                        int playersNumber = Integer.parseInt(message);
                        String UID = Utils.UIDGenerator();
                        Lobby lobby = new Lobby(this, UID, playersNumber);
                        server.getLobbyList().add(lobby);
                        System.out.println("New lobby created ["+UID+"]");
                        server.getUserGame().replace(nickname, lobby.getLobbyUID());
                        lobbyCreated = true;
                        lobby.run();
                    }
                    case "2" -> {
                        sendInfoMessage("* GAME LOADING *\n\n");
                        if (!server.getUserGame().containsKey(nickname) || server.getUserGame().get(nickname).equals("-")){
                            sendDeny("No game found");
                        }
                        else {
                            Lobby lobby = new Lobby(this,server.getUserGame().get(nickname));
                            sendInfoMessage("Joining game "+server.getUserGame().get(nickname));
                            server.getLobbyList().add(lobby);
                            System.out.println("New lobby created ["+lobby.getLobbyUID()+"]");
                            lobbyCreated = true;
                            lobby.run();
                        }
                    }
                    case "3" -> {
                        sendInfoMessage("* GAME JOINING *\n\n");
                        List<Lobby> lobbyList;
                        boolean flag = false;
                        synchronized (server.getLobbyList()){
                            lobbyList = server.getLobbyList();
                        }
                        for(Lobby l : lobbyList){
                            if(l.isOpen()){
                                System.out.println(nickname + "joined game "+l.getLobbyUID());
                                server.getUserGame().replace(nickname, l.getLobbyUID());
                                flag = true;
                                lobbyCreated = true;
                                l.acceptPlayer(this);
                                break;
                            }
                        }
                        if(!flag){
                            sendInfoMessage("No game available. You should create a new one");
                        }
                    }
                    case "4" ->{
                        List<Lobby> filteredLobbyList = server.getLobbyList().stream()
                                .filter(l -> l.getGameMode() == Lobby.GameMode.SAVEDGAME)
                                .filter(l -> l.getLobbyUID().equals(server.getUserGame().get(nickname)))
                                .toList();
                        if(filteredLobbyList.size() > 1){
                            throw new RuntimeException("UID not unique");
                        }
                        else if(filteredLobbyList.size() == 1){
                            Lobby lobby = filteredLobbyList.get(0);
                            sendInfoMessage("Game "+ lobby.getLobbyUID() + "started by " + lobby.getLobbyPlayers().get(0).nickname);
                            sendInfoMessage("Would you like to join? [y/n]");

                            System.out.println("Saved game for "+nickname+" found");

                            action = getAction();

                            while(!action.getActionType().equals(Action.ActionType.INFO) && !action.getInfo().toUpperCase().equals("Y") && !action.getInfo().toUpperCase().equals("N")){
                                sendDeny("Type the right key...");
                                action = getAction();
                            }
                            message = action.getInfo();
                            if(message.toUpperCase().equals("Y")) {
                                lobby.broadcastMessage(nickname+ " joined");
                                System.out.println(nickname + "joined game "+lobby.getLobbyUID());
                                server.getUserGame().replace(nickname, lobby.getLobbyUID());
                                filteredLobbyList.get(0).acceptPlayer(this);
                            }
                            else{
                                System.out.println(nickname + "rejected invitation from " + lobby.getLobbyUID());
                                lobby.broadcastMessage(nickname+" has rejected the invitation.");
                                lobby.broadcastMessage("Game is closing...");
                                lobby.shutdown();
                                server.getLobbyList().remove(lobby);
                                System.out.println("Lobby "+lobby.getLobbyUID()+" killed.");
                            }
                        }else{
                            sendInfoMessage("No lobby was found.");
                        }

                    }
                    case "0" -> {
                        sendInfoMessage("Closing...");
                        server.removeClient(this);
                        server.getUserGame().remove(nickname);
                        shutdown();
                        System.out.println(nickname+" disconnected");
                    }
                }
            }
                //firt case: finding a saved game



        } catch (Exception e) {
            server.removeClient(this);
            server.getUserGame().remove(nickname);
            shutdown();
            System.err.println("Client lost connection");
            //handle lost connection, save and close game.
        }
    }

    public String getNickname() {
        return nickname;
    }

    public Socket getClientSocket() {
        return clientSocket;
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

    public synchronized void sendInfoMessage(String message) {
        Gson gson = new Gson();
        try {
            out.println(gson.toJson(new Response(message)));
        } catch (Exception e) {
            System.out.println("Error occurred while sending a message: " + e.toString());
            e.printStackTrace();
        }
    }

    public synchronized void sendDeny(String message) {
        Gson gson = new Gson();
        try {
            out.println(gson.toJson(new Response(Response.ResponseType.DENIED, message)));
        } catch (Exception e) {
            System.out.println("Error occurred while sending a message: " + e.toString());
            e.printStackTrace();
        }
    }

    public synchronized void sendAccept(String message) {
        Gson gson = new Gson();
        try {
            out.println(gson.toJson(new Response(Response.ResponseType.VALID, message)));
        } catch (Exception e) {
            System.out.println("Error occurred while sending a message: " + e.toString());
            e.printStackTrace();
        }
    }

    public synchronized void sendUpdateRequest() {
        //TODO IMPLEMENTATION
    }
}

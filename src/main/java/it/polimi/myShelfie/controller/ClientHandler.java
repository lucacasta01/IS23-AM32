package it.polimi.myShelfie.controller;

import com.google.gson.Gson;
import it.polimi.myShelfie.application.Server;
import it.polimi.myShelfie.utilities.ANSI;
import it.polimi.myShelfie.utilities.JsonParser;
import it.polimi.myShelfie.utilities.Utils;
import it.polimi.myShelfie.utilities.beans.Action;
import it.polimi.myShelfie.utilities.beans.Response;
import it.polimi.myShelfie.utilities.beans.View;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.rmi.Remote;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Map;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private String nickname="/";
    private Registry registry;
    private boolean isRMI = false;
    private Remote client;
    private BufferedReader in;
    private PrintWriter out;
    private Server server;
    private boolean isPlaying = false;
    private String color;


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
            Action action;
            if(nickname.equals("/")){
                sendInfoMessage("Please insert your username");
                 action = getAction();

                nickname = action.getInfo();
                while(action.getActionType() != Action.ActionType.INFO || server.isConnected(nickname)) {
                    if(action.getActionType() == Action.ActionType.INFO) {
                        sendDeny("Nickname " + nickname + " already used, retry:");
                    }else if(action.getActionType()== Action.ActionType.CHAT){
                        sendDeny("The chat is not active now");
                    }else if(action.getActionType()== Action.ActionType.PICKTILES){
                        sendDeny("Cannot pick tiles here, game not started");
                    }else if(action.getActionType()== Action.ActionType.SELECTCOLUMN){
                        sendDeny("Cannot select column here, game not started");
                    }
                    else if(action.getActionType() == Action.ActionType.QUIT){
                        sendShutdown();
                        shutdown();
                    }

                    action = getAction();
                    if(action.getActionType() == Action.ActionType.INFO) {
                        nickname = action.getInfo();
                    }

                }
                synchronized (server.getUserGame()){
                    if(!server.getUserGame().containsKey(nickname)) {
                        server.getUserGame().put(nickname, "-");
                    }
                }
                sendAccept("Username accepted");

                System.out.println(nickname + " connected to the server");
            }

            boolean lobbyCreated = false;
            String message;
            String chose;

            sendMenu();
            while((action=getAction())!=null){
                if(server.lobbyOf(this)==null){
                    lobbyCreated=false;
                }
                if(!lobbyCreated){
                    chose=action.getInfo();
                    while(action.getActionType() != Action.ActionType.INFO ||(action.getActionType()== Action.ActionType.INFO&&(!chose.equals("1") && !chose.equals("2") && !chose.equals("3") && !chose.equals("4") && !chose.equals("0")))){
                        if(action.getActionType() == Action.ActionType.INFO) {
                            sendDeny("Type the right key");
                        }else if(action.getActionType()== Action.ActionType.CHAT){
                            sendDeny("The chat is not active now");
                        }else if(action.getActionType()== Action.ActionType.PICKTILES){
                            sendDeny("Cannot pick tiles here, game not started");
                        }else if(action.getActionType()== Action.ActionType.SELECTCOLUMN){
                            sendDeny("Cannot select column here, game not started");
                        }
                        else if(action.getActionType() == Action.ActionType.QUIT){
                            sendShutdown();
                            shutdown();
                            System.out.println(nickname + " disconnected");
                        }
                        action = getAction();
                        chose = action.getInfo();
                    }

                        //sendAccept("Game mode selected");
                        switch (chose) {
                            case "1" -> {
                                sendInfoMessage("* CREATE NEW GAME *\n");



                                String UID = Utils.UIDGenerator();
                                Lobby lobby = new Lobby(this, UID, 0);
                                System.out.println("New lobby created [" + UID + "]");
                                server.getLobbyList().add(lobby);
                                synchronized (server.getUserGame()) {
                                    server.getUserGame().put(nickname, lobby.getLobbyUID());
                                    server.saveUserGame();
                                }
                                lobbyCreated = true;
                                server.runLobby(lobby);
                            }
                            case "2" -> {
                                sendInfoMessage("* GAME LOADING *\n");
                                Map<String, String> userGame;
                                synchronized (server.getUserGame()) {
                                    userGame = server.getUserGame();
                                }

                                if (!userGame.containsKey(nickname) || userGame.get(nickname).equals("-")) {
                                    sendDeny("No game was found");
                                    sendMenu();
                                } else {
                                    Lobby lobby = new Lobby(this, userGame.get(nickname));
                                    sendInfoMessage("Joining game " + userGame.get(nickname));
                                    synchronized (server.getLobbyList()) {
                                        server.getLobbyList().add(lobby);
                                    }
                                    System.out.println("New lobby created [" + lobby.getLobbyUID() + "]");
                                    lobbyCreated = true;
                                    server.runLobby(lobby);
                                }
                            }
                            case "3" -> {
                                sendInfoMessage("* GAME JOINING *\n");
                                List<Lobby> lobbyList;
                                boolean flag = false;
                                synchronized (server.getLobbyList()) {
                                    lobbyList = server.getLobbyList();
                                }
                                for (Lobby l : lobbyList) {
                                    if (l.isOpen()) {
                                        System.out.println(nickname + " joined game " + l.getLobbyUID());
                                        synchronized (server.getUserGame()) {
                                            server.getUserGame().put(nickname, l.getLobbyUID());
                                            server.saveUserGame();
                                        }
                                        flag = true;
                                        lobbyCreated = true;
                                        l.acceptPlayer(this);
                                        break;
                                    }
                                }
                                if (!flag) {
                                    sendInfoMessage("No game available. You should create a new one");
                                    sendMenu();
                                }
                            }
                            case "4" -> {
                                List<Lobby> filteredLobbyList = server.getLobbyList().stream()
                                        .filter(l -> l.getGameMode() == Lobby.GameMode.SAVEDGAME)
                                        .filter(l -> l.getLobbyUID().equals(server.getUserGame().get(nickname)))
                                        .toList();
                                if (filteredLobbyList.size() > 1) {
                                    throw new RuntimeException("UID not unique");
                                } else if (filteredLobbyList.size() == 1) {
                                    Lobby lobby = filteredLobbyList.get(0);
                                    sendInfoMessage("Game " + lobby.getLobbyUID() + " started by " + lobby.getLobbyPlayers().get(0).nickname);
                                    sendInfoMessage("Would you like to join? [y/n]");

                                    System.out.println("Saved game for " + nickname + " found");

                                    action = getAction();

                                    while (!action.getInfo().toUpperCase().equals("Y") && !action.getInfo().toUpperCase().equals("N")) {
                                        sendDeny("Type the right key...");
                                        action = getAction();
                                    }
                                    message = action.getInfo();
                                    if (message.toUpperCase().equals("Y")) {
                                        lobby.broadcastMessage(nickname + " joined");
                                        System.out.println(nickname + " joined game " + lobby.getLobbyUID());
                                        filteredLobbyList.get(0).acceptPlayer(this);
                                    } else {
                                        System.out.println(nickname + " rejected invitation from " + lobby.getLobbyUID());
                                        lobby.broadcastMessage(nickname + " has rejected the invitation.");
                                        lobby.broadcastMessage("Game is closing...");
                                        server.killLobby(lobby.getLobbyUID());
                                        //System.out.println("Lobby " + lobby.getLobbyUID() + " killed.");
                                    }
                                } else {
                                    sendInfoMessage("No lobby was found.");
                                    sendMenu();
                                }

                            }
                            case "0" -> {
                                sendShutdown();
                                server.removeClient(this);
                                shutdown();
                                System.out.println(nickname + " disconnected");
                            }
                        }


                }else{
                    for(Lobby l:server.getLobbyList()){
                        for(ClientHandler ch: l.getLobbyPlayers()){
                            if(ch.equals(this)){
                                synchronized (l.actions){
                                    if(action.getActionType() != Action.ActionType.QUIT) {
                                        l.recieveAction(action);
                                        l.actions.notifyAll();
                                    }
                                    else{
                                        server.killLobby(l.getLobbyUID());
                                    }
                                }
                            }
                        }
                    }
                }

            }


            //firt case: finding a saved game



        } catch (Exception e) {
            server.removeClient(this);
            shutdown();
            System.err.println("Client lost connection");
            synchronized (server.getUserGame()){
                server.saveUserGame();
            }

            server.getConnectedClients().remove(this);
            if(server.getUserGame()!=null){
                if(server.getUserGame().get(this.nickname)!=null) {
                    if (server.getUserGame().get(this.nickname).equals("-")) {
                        server.getUserGame().remove(this.nickname);
                    }
                }
            }
            this.shutdown();
            if(server.lobbyOf(this)!=null){
                Lobby lobby =server.lobbyOf(this);
                server.killLobby(lobby.getLobbyUID());
            }

            //handle lost connection, save and close game.
        }
    }

    public String getNickname() {
        return nickname;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public synchronized boolean isPlaying() {
        return isPlaying;
    }

    public synchronized void setPlaying(boolean playing) {
        isPlaying = playing;
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
            out.println(gson.toJson(new Response(Response.ResponseType.INFO,null,null,message)));
        } catch (Exception e) {
            System.out.println("Error occurred while sending a message: " + e.toString());
            e.printStackTrace();
        }
    }

    public synchronized void sendChatMessage(String message, String sender){
        Response r = new Response(Response.ResponseType.CHATMESSAGE, new Response.ChatMessage(sender, message), null, null);
        Gson gson = new Gson();
        try {
            out.println(gson.toJson(r));
        } catch (Exception e) {
            System.out.println("Error occurred while sending a message: " + e.toString());
            e.printStackTrace();
        }
    }

    public synchronized void sendDeny(String message) {
        Gson gson = new Gson();
        try {
            out.println(gson.toJson(new Response(Response.ResponseType.DENIED, null ,null,message)));
        } catch (Exception e) {
            System.out.println("Error occurred while sending a message: " + e.toString());
            e.printStackTrace();
        }
    }

    public synchronized void sendAccept(String message) {
        Gson gson = new Gson();
        try {
            out.println(gson.toJson(new Response(Response.ResponseType.VALID, new Response.ChatMessage(this.nickname, ""),null, message)));
        } catch (Exception e) {
            System.out.println("Error occurred while sending a message: " + e.toString());
            e.printStackTrace();
        }
    }

    public synchronized void sendPing() throws IOException{
        Gson gson = new Gson();
        out.println(gson.toJson(new Response(Response.ResponseType.PING, null,null, "ping")));
    }

    public synchronized void sendShutdown(){
        Gson gson = new Gson();
        out.println(gson.toJson(new Response(Response.ResponseType.SHUTDOWN, null,null, "Closing...")));
    }

    public synchronized void sendView(View view) {
        Gson gson = new Gson();
        out.println(gson.toJson(new Response(Response.ResponseType.UPDATE, null, view, null)));
    }

    public synchronized void sendHelpMessage(){
        /*String[][] data = {
                { "Command", "Arguments", "Note"},
                { "/chat", "text message","use it to send a chat message to other players"},
                { "/collect", "r1,c1 (r2,c2) (r3,c3)", "game command to collect tiles from the board"},
                { "/order", "C1 C2 C3", "use it if you want change the tiles order before insert them in your shelf"},
                {"/column", "col number", "game command to insert the tiles you have collected into your shelf at the selected column"},
                {"/quit","-","exit from the game"}
        };

        int[] colWidths = { 5, 10, 10 };

        for (String[] row : data) {
            for (int i = 0; i < row.length; i++) {
                System.out.printf("%-" + colWidths[i] + "s", row[i]);
            }
            System.out.println();
        }*/

        StringBuilder string = new StringBuilder();
        string.append("\t\t\t\t\t\t"+ ANSI.BOLD+ANSI.ITALIQUE+"****** MY SHELFIE ******"+ANSI.RESET_STYLE+"\n\n");
        string.append(ANSI.BOLD+"Command\t\t\tArguments\t\t\t\tNotes"+ANSI.RESET_STYLE+"\n");
        string.append("/chat\t\t\ttext message\t\t\tuse it to send a chat message to other players\n");
        string.append("/collect\t\tr1,c1 (r2,c2) (r3,c3)\t\tgame command to collect tiles from the board\n");
        string.append("/order\t\t\tC1 C2 C3\t\t\t\tuse it if you want change the tiles order before insert them in your shelf\n");
        string.append("/column\t\t\tcol number\t\t\t\tgame command to insert the tiles you have collected into your shelf at the selected column\n");
        string.append("/quit\t\t\t-\t\t\t\t\t\texit from the game\n");
        sendInfoMessage(string.toString());
    }
    public void sendMenu(){
        sendInfoMessage("\n(1) New Game");
        sendInfoMessage("(2) Load last game");
        sendInfoMessage("(3) Join random game");
        sendInfoMessage("(4) Search for started saved game");
        sendInfoMessage("(0) Exit\n");
    }
}

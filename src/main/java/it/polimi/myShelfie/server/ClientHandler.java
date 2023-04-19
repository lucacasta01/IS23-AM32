package it.polimi.myShelfie.server;

import it.polimi.myShelfie.utilities.Utils;

import java.io.BufferedReader;
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
            while(server.getUserGame().containsKey(nickname)){
                out.println("Nickname "+nickname+" used, retry:");
                nickname = in.readLine();
            }
            server.getUserGame().put(nickname, "/");
            System.out.println(nickname + " connected to the server");
            server.broadcastMessage(nickname + " joined");
            String message;

            //finding an old game
            List<Lobby> filteredLobbyList = server.getLobbyList().stream()
                    .filter(l -> l.getGameMode() == Lobby.GameMode.SAVEDGAME)
                    .filter(l -> l.getLobbyUID().equals(server.getUserGame().get(nickname)))
                    .toList();
            if(filteredLobbyList.size() > 1){
                throw new RuntimeException("UID not unique");
            }
            else if(filteredLobbyList.size() == 1){
                Lobby lobby = filteredLobbyList.get(0);
                out.println("Game "+ lobby.getLobbyUID() + "started by " + lobby.getLobbyPlayers().get(0).nickname);
                out.println("Would you like to join? [y/n]");

                System.out.println("Saved game for "+nickname+" found");

                while(!(message = in.readLine()).toUpperCase().equals("Y") && !(message = in.readLine()).toUpperCase().equals("N")){
                    out.println("Type the right key...");
                }
                if(message.toUpperCase().equals("Y")) {
                    filteredLobbyList.get(0).acceptPlayer(this);
                    lobby.broadcastMessage(nickname+ " joined");
                    System.out.println(nickname + "joined game "+lobby.getLobbyUID());
                }
                else{
                    System.out.println(nickname + "rejected invitation from " + lobby.getLobbyUID());
                    lobby.broadcastMessage(nickname+" has rejected the invitation.");
                    lobby.broadcastMessage("Game is closing...");
                    lobby.shutdown();
                    server.getLobbyList().remove(lobby);
                    System.out.println("Lobby "+lobby.getLobbyUID()+" killed.");
                }
            }
            //Lobby not found ->
            else{
                String chose = "1";
                while(!chose.equals("4")) {
                    out.println("(1) New Game");
                    out.println("(2) Load last game");
                    out.println("(3) Join random game");
                    out.println("(4) Exit\n");
                    chose = in.readLine();
                    while (!chose.equals("1") && !chose.equals("2") && !chose.equals("3") && !chose.equals("4")) {
                        out.println("Type the right key...");
                        chose = in.readLine();
                    }

                    switch (chose) {
                        case "1" -> {
                            out.println("* CREATE NEW GAME *\n\n");
                            out.println("Enter number of players: ");
                            message = in.readLine();
                            while (!message.equals("2") && !message.equals("3") && !message.equals("4")) {
                                out.println("Invalid number. Retry");
                                message = in.readLine();
                            }
                            int playersNumber = Integer.parseInt(message);
                            String UID = Utils.UIDGenerator();
                            Lobby lobby = new Lobby(this, UID, playersNumber);
                            server.getLobbyList().add(lobby);
                            lobby.run();
                            System.out.println("New lobby created ["+UID+"]");
                        }
                        case "2" -> {
                            out.println("* GAME LOADING *\n\n");
                            if (!server.getUserGame().containsKey(nickname)) {
                                out.println("No game found");
                            }
                            else {
                                Lobby lobby = new Lobby(this,server.getUserGame().get(nickname));
                                out.println("Joining game "+server.getUserGame().get(nickname));
                                lobby.run();
                                server.getLobbyList().add(lobby);
                                System.out.println("New lobby created ["+lobby.getLobbyUID()+"]");
                            }
                        }
                        case "3" -> {
                            out.println("* GAME JOINING *\n\n");
                            List<Lobby> lobbyList;
                            boolean flag = false;
                            synchronized (server.getLobbyList()){
                                lobbyList = server.getLobbyList();
                            }
                            for(Lobby l : lobbyList){
                                if(l.isOpen()){
                                    l.acceptPlayer(this);
                                    l.broadcastMessage(nickname+ " joined");
                                    System.out.println(nickname + "joined game "+l.getLobbyUID());
                                    flag = true;
                                    break;
                                }
                            }
                            if(!flag){
                                out.println("No game available. You should create a new one");
                            }
                        }
                        case "4" -> {
                            out.println("Closing...");
                            server.removeClient(this);
                            server.getUserGame().remove(nickname);
                            shutdown();
                            System.out.println(nickname+" disconnected");
                        }
                    }
                }
                //firt case: finding a saved game
            }

            
        } catch (Exception e) {
            server.removeClient(this);
            server.getUserGame().remove(nickname);
            shutdown();
            System.err.println("Client lost connection");
            //handle lost connection, save and close game.
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

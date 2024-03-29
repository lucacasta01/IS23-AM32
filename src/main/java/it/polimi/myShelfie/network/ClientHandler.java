package it.polimi.myShelfie.network;
import com.google.gson.Gson;
import it.polimi.myShelfie.application.Server;
import it.polimi.myShelfie.network.RMI.RMIClient;
import it.polimi.myShelfie.utilities.*;
import it.polimi.myShelfie.utilities.pojo.Action;
import it.polimi.myShelfie.utilities.pojo.ChatMessage;
import it.polimi.myShelfie.utilities.pojo.Response;
import it.polimi.myShelfie.view.View;
import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This is the network rep of the player.
 * This class is useful to make the client-server communication possible
 * Is important to remember that each ClientHandler is executed as a stand-alone thread.
 * For each Player connected, a new ClientHandler thread is executed on Server pool.
 */
public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private String nickname="/";
    private Boolean isRMI = false;
    private BufferedReader in;
    private PrintWriter out;
    private final Server server;
    private boolean isPlaying = false;
    private String color;
    private final List<PingObject> pongResponses = new ArrayList<>();
    private final List<Action> rmiActions;
    private RMIClient rmiClient;
    public final Object locker = new Object();




    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.server = Server.getInstance();
        this.rmiClient = null;
        this.rmiActions = null;
    }
    public ClientHandler() {
        this.clientSocket = null;
        this.server = Server.getInstance();
        this.isRMI = true;
        this.rmiActions = new ArrayList<>();
    }

    /**
     * method to close connection between server and client
     */
    public void shutdown() {
        if(!isRMI) {
            try {
                in.close();
                out.close();
                if (!clientSocket.isClosed()) {
                    clientSocket.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            synchronized (server.getConnectedClients()) {
                if(server.getConnectedClients().get(this) != null) {
                    server.getConnectedClients().get(this).setKill(true);
                }
            }
        }
        server.removeClient(this);
    }

    public List<Action> getRmiActions() {
        return rmiActions;
    }

    public void setRmiAction(Action rmiAction) {
        this.rmiActions.add(rmiAction);
    }

    /**
     * method that returns and removes a specific Action from the Action's list
     * @return the specific Action
     */
    public Action getAction(){
        if(!isRMI) {
            try {
                Action a = JsonParser.getAction(in.readLine());
                if (a == null) {
                    return new Action(Action.ActionType.VOID, null, null, null, null, null);
                }
                return a;
            } catch (Exception e) {
                return new Action(Action.ActionType.VOID, null, null, null, null, null);
            }
        }else{
            if(rmiActions.size()==0){
                synchronized (rmiActions){
                    try {
                        rmiActions.wait();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
            Action toReturn = rmiActions.get(0);
            rmiActions.remove(0);
            return toReturn;
        }
    }



    public void run() {
        if(!isRMI){ //TCP
            try {
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                synchronized (locker){
                    locker.notifyAll();
                }
            } catch (Exception e) {
                System.err.println("Exception throws during stream creation: ");
                e.printStackTrace();
            }
        }
        gameLoop();
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    private void gameLoop(){
        try {
            Action action;
            if(nickname.equals("/")){
                sendInfoMessage("Please insert your username");
                action = getAction();
                String nickname = action.getInfo();
                while(action.getActionType() != Action.ActionType.INFO || server.isConnected(nickname) || !Utils.checkNicknameFormat(nickname)) {
                    if(action.getActionType() == Action.ActionType.INFO) {
                        if(!Utils.checkNicknameFormat(nickname)){
                            notifyNicknameDeny("Invalid nickname");
                        }
                        else if(server.isConnected(nickname)){
                            notifyNicknameDeny("Nickname already used");
                        }

                    }else if(action.getActionType()== Action.ActionType.CHAT){
                        sendDeny("The chat is not active now");
                    }else if(action.getActionType()== Action.ActionType.PICKTILES){
                        sendDeny("Cannot pick tiles here, game not started");
                    }else if(action.getActionType()== Action.ActionType.SELECTCOLUMN){
                        sendDeny("Cannot select column here, game not started");
                    }
                    else if(action.getActionType() == Action.ActionType.QUIT){
                        if(!isRMI) {
                            server.getConnectedClients().get(this).setElapsed();
                            sendShutdown();
                            shutdown();
                        }else{
                            server.getConnectedClients().get(this).setKill(true);
                            server.getConnectedClients().remove(this);
                            sendShutdown();
                            shutdown();
                        }
                    }else if(action.getActionType() == Action.ActionType.PING){
                        sendPong(action.getInfo());
                    } else if (action.getActionType() == Action.ActionType.PONG) {
                        addPong();
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
                this.nickname = nickname;
                sendAccept("Username accepted");
                notifyNicknameAccept();
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
                            if(!isRMI) {
                                server.getConnectedClients().get(this).setElapsed();
                                sendShutdown();
                                shutdown();
                            }else{
                                sendShutdown();
                                shutdown();
                            }
                            System.out.println(nickname + " disconnected");
                        }else if(action.getActionType() == Action.ActionType.PING){
                            sendPong(action.getInfo());
                        }
                        else if (action.getActionType() == Action.ActionType.PONG) {
                            addPong();
                        }
                        action = getAction();
                        if(action !=null){
                            chose = action.getInfo();
                        }

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
                                if(server.getUserGame().containsKey(this.nickname)){
                                    if(!server.getUserGame().get(this.nickname).equals("-")){
                                        String oldUID = server.getUserGame().get(this.nickname);
                                        server.getUserGame().entrySet().removeIf(entry -> entry.getValue().equals(oldUID));
                                        Path savingFilePath = Paths.get(System.getProperty("user.dir") + "/config/savedgames/"+ oldUID + ".json");
                                        if(savingFilePath.toFile().isFile()){
                                            try {
                                                Files.delete(savingFilePath);
                                                System.out.println("Old game file deleted successfully, UID: "+ oldUID);
                                            }
                                            catch (IOException e){
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                }
                                server.getUserGame().put(nickname, lobby.getLobbyUID());
                                server.saveUserGame();
                            }
                            lobbyCreated = true;
                            this.isPlaying=true;
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
                                denyLoadGame();
                                sendMenu();
                            } else if(server.getLobbyList().stream().filter(lobby -> lobby.getLobbyUID().equals(userGame.get(nickname))).toList().size()>0){
                                sendDeny("Another client started this game, retry to connect...");
                                denyLoadGame();
                                sendMenu();
                            } else {
                                Lobby lobby = new Lobby(this, userGame.get(nickname));
                                synchronized (server.getLobbyList()) {
                                    sendInfoMessage("Joining game " + userGame.get(nickname));
                                    server.getLobbyList().add(lobby);
                                }
                                System.out.println("New lobby created [" + lobby.getLobbyUID() + "]");
                                lobbyCreated = true;
                                this.isPlaying=true;
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
                                    this.isPlaying=false;
                                    l.acceptPlayer(this);
                                    break;
                                }
                            }
                            if (!flag) {
                                sendInfoMessage("No game available. You should create a new one");
                                denyRandomGame();
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
                                oldGameFound();
                                System.out.println("Saved game for " + nickname + " found");

                                action = getAction();

                                while ((action.getActionType()!= Action.ActionType.INFO)||(action.getActionType()== Action.ActionType.INFO&&(!action.getInfo().equalsIgnoreCase("Y")&&!action.getInfo().equalsIgnoreCase("N")))){
                                    if(action.getActionType() == Action.ActionType.INFO) {
                                        sendDeny("Type the right key...");
                                    }else if(action.getActionType()== Action.ActionType.CHAT){
                                        sendDeny("The chat is not active now");
                                    }else if(action.getActionType()== Action.ActionType.PICKTILES){
                                        sendDeny("Cannot pick tiles here, game not started");
                                    }else if(action.getActionType()== Action.ActionType.SELECTCOLUMN){
                                        sendDeny("Cannot select column here, game not started");
                                    }
                                    else if(action.getActionType() == Action.ActionType.QUIT){
                                        if(!isRMI) {
                                            server.getConnectedClients().get(this).setElapsed();
                                            sendShutdown();
                                            shutdown();
                                        }else{
                                            sendShutdown();
                                            shutdown();
                                        }
                                    }else if(action.getActionType() == Action.ActionType.PING){
                                        sendPong(action.getInfo());
                                    } else if (action.getActionType() == Action.ActionType.PONG){
                                        addPong();
                                    }

                                    action = getAction();
                                }
                                message = action.getInfo();
                                if (message.equalsIgnoreCase("Y")) {
                                    lobby.broadcastMessage(nickname + " joined");
                                    System.out.println(nickname + " joined game " + lobby.getLobbyUID());
                                    this.isPlaying=true;
                                    filteredLobbyList.get(0).acceptPlayer(this);
                                    lobbyCreated=true;
                                } else {
                                    System.out.println(nickname + " rejected invitation from " + lobby.getLobbyUID());
                                    lobby.broadcastMessage(nickname + " has rejected the invitation.");
                                    lobby.broadcastMessage("Game is closing...");
                                    server.killLobby(lobby.getLobbyUID());
                                    //System.out.println("Lobby " + lobby.getLobbyUID() + " killed.");
                                }
                            } else {
                                sendInfoMessage("No lobby was found.");
                                oldGameNotFound();
                                sendMenu();
                            }

                        }
                        case "0" -> {
                            if(!isRMI) {
                                server.getConnectedClients().get(this).setElapsed();
                            }
                            System.out.println(nickname + " disconnected");
                            sendShutdown();
                            shutdown();
                        }
                    }


                }else{
                    Lobby l = server.lobbyOf(this);

                    if(l!=null){
                        synchronized (l.actions){
                            if(action.getActionType()== Action.ActionType.PING){
                                sendPong(action.getInfo());
                            }
                            else if (action.getActionType() == Action.ActionType.PONG) {
                                addPong();
                            }
                            else if(action.getActionType() == Action.ActionType.QUIT){
                                if(!isRMI){
                                    server.getConnectedClients().get(this).setElapsed();
                                    server.getConnectedClients().remove(this);
                                    System.out.println(ANSI.RED+nickname+" left the game"+ANSI.RESET_COLOR);
                                }else{
                                    server.getConnectedClients().get(this).setKill(true);
                                    server.getConnectedClients().remove(this);
                                    l.getLobbyPlayers().remove(this);
                                    System.out.println(ANSI.RED+nickname+" left the game"+ANSI.RESET_COLOR);
                                }
                                l.notifyExit();
                                server.killLobby(l.getLobbyUID());
                            }else if(action.getActionType()== Action.ActionType.REQUEST_MENU){
                                l.recieveAction(action);
                                l.actions.notifyAll();
                                isPlaying=false;
                                l.setEndWaitingPlayers();
                                lobbyCreated=false;
                                synchronized (server.getLobbyList()) {
                                    server.getLobbyList().remove(server.lobbyOf(this));
                                }
                            }else{
                                l.recieveAction(action);
                                l.actions.notifyAll();
                            }
                        }
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void denyRandomGame() {
        if(isRMI){
            try{
                rmiClient.denyRandomGame();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        else {
            Response r = new Response(Response.ResponseType.RANDOM_GAME_NOT_FOUND, null, null, null);
            Gson gson = new Gson();
            try {
                out.println(gson.toJson(r));
            } catch (Exception e) {
                System.out.println("Error occurred while sending a message: ");
                e.printStackTrace();
            }
        }
    }

    private void oldGameNotFound() {
        if(isRMI){
            try{
                rmiClient.oldGameNotFound();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        else {
            Response r = new Response(Response.ResponseType.OLD_GAME_NOT_FOUND, null, null, null);
            Gson gson = new Gson();
            try {
                out.println(gson.toJson(r));
            } catch (Exception e) {
                System.out.println("Error occurred while sending a message: ");
                e.printStackTrace();
            }
        }
    }

    private synchronized void oldGameFound() {
        if(isRMI){
            try{
                rmiClient.foundOldGame();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        else {
            Response r = new Response(Response.ResponseType.FOUND_OLD_GAME, null, null, null);
            Gson gson = new Gson();
            try {
                out.println(gson.toJson(r));
            } catch (Exception e) {
                System.out.println("Error occurred while sending a message: ");
                e.printStackTrace();
            }
        }
    }

    public synchronized void denyLoadGame() {
        if(isRMI){
            try{
                rmiClient.denyLoadGame();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        else {
            Response r = new Response(Response.ResponseType.DENY_LOAD_GAME, null, null, null);
            Gson gson = new Gson();
            try {
                out.println(gson.toJson(r));
            } catch (Exception e) {
                System.out.println("Error occurred while sending a message: ");
                e.printStackTrace();
            }
        }
    }

    public String getNickname() {
        return nickname;
    }

    public String getColor() {
        return color;
    }
    public boolean isRMI(){
        return this.isRMI;
    }

    public void setColor(String color) {
        this.color = color;
    }
    public synchronized boolean isPlaying() {
        return isPlaying;
    }

    public synchronized void setPlaying(boolean playing) {
        isPlaying = playing;
    }
    public synchronized void sendInfoMessage(String message) {
        if(isRMI){
            try{
                rmiClient.infoMessage(message);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        else {
            Gson gson = new Gson();
            try {
                out.println(gson.toJson(new Response(Response.ResponseType.INFO, null, null, message)));
            } catch (Exception e) {
                System.out.println("Error occurred while sending a message: ");
                e.printStackTrace();
            }
        }
    }

    /**
     * method to send the message passed as a parameter
     * @param chatMessage
     */
    public synchronized void sendChatMessage(ChatMessage chatMessage){
        if(isRMI){
            try{
                rmiClient.chatMessage(chatMessage);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        else {
            Response r = new Response(Response.ResponseType.CHATMESSAGE, chatMessage, null, null);
            Gson gson = new Gson();
            try {
                out.println(gson.toJson(r));
            } catch (Exception e) {
                System.out.println("Error occurred while sending a message: ");
                e.printStackTrace();
            }
        }
    }

    /**
     * method used to notify the client that the nickname has been accepted
     */
    public synchronized void notifyNicknameAccept(){
        if(isRMI){
            try{
                rmiClient.nicknameAccepted();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        else {
            Response r = new Response(Response.ResponseType.NICKNAME_ACCEPTED, new ChatMessage(nickname, "",""), null, null);
            Gson gson = new Gson();
            try {
                out.println(gson.toJson(r));
            } catch (Exception e) {
                System.out.println("Error occurred while sending a message: ");
                e.printStackTrace();
            }
        }
    }

    /**
     * method used to notify the client that the nickname has been denied
     * @param message
     */
    public synchronized void notifyNicknameDeny(String message){
        if(isRMI){
            try{
                rmiClient.nicknameDenied(message);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        else {
            sendDeny(message);
            Response r = new Response(Response.ResponseType.NICKNAME_DENIED, new ChatMessage(nickname, "",""), null, message);
            Gson gson = new Gson();
            try {
                out.println(gson.toJson(r));
            } catch (Exception e) {
                System.out.println("Error occurred while sending a message: ");
                e.printStackTrace();
            }
        }
    }

    /**
     * method used to notify the client that the lobby has been created
     * @param playersNumber
     */
    public synchronized void notifyLobbyCreated(int playersNumber) {
        if(isRMI){
            try{
                rmiClient.notifyLobbyCreated(Integer.toString(playersNumber));
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        else {
            Gson gson = new Gson();
            try {
                out.println(gson.toJson(new Response(Response.ResponseType.LOBBY_CREATED, null, null, Integer.toString(playersNumber))));
            } catch (Exception e) {
                System.out.println("Error occurred while sending a message: ");
                e.printStackTrace();
            }
        }
    }

    /**
     * method used to notify the client that the lobby has been joined
     * @param waitPlayerStatus
     */
    public synchronized void notifyLobbyJoined(String waitPlayerStatus){
        if(isRMI){
            try{
                rmiClient.notifyGameJoined();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        else {
            Gson gson = new Gson();
            try {
                out.println(gson.toJson(new Response(Response.ResponseType.LOBBY_JOINED, null, null, waitPlayerStatus)));
            } catch (Exception e) {
                System.out.println("Error occurred while sending a message: ");
                e.printStackTrace();
            }
        }
    }

    /**
     * method used to notify the client that the lobby has started
     */
    public synchronized void notifyGameStarted() {
        if(isRMI){
            try{
                rmiClient.notifyGameStarted();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        else {
            Gson gson = new Gson();
            try {
                out.println(gson.toJson(new Response(Response.ResponseType.GAME_STARTED, null, null, null)));
            } catch (Exception e) {
                System.out.println("Error occurred while sending a message: ");
                e.printStackTrace();
            }
        }
    }

    /**
     *
     * @param message
     */
    public synchronized void sendDeny(String message) {
        if(isRMI){
            try {
                rmiClient.denied(message);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        else {
            Gson gson = new Gson();
            try {
                out.println(gson.toJson(new Response(Response.ResponseType.DENIED, null, null, message)));
            } catch (Exception e) {
                System.out.println("Error occurred while sending a message: ");
                e.printStackTrace();
            }
        }
    }

    /**
     *
     */
    public void addPong(){
        synchronized (pongResponses){
            pongResponses.add(new PingObject(false));
            pongResponses.notifyAll();
        }
    }

    /**
     *
     * @param count
     */
    public synchronized  void sendPong(String count){
        Gson gson = new Gson();
        try {
            out.println(gson.toJson(new Response(Response.ResponseType.PONG, null ,null,count)));
        } catch (Exception e) {
            System.out.println("Error occurred while sending a pong: ");
            e.printStackTrace();
        }
    }

    /**
     *
     * @param message
     */
    public synchronized void sendAccept(String message) {
        if(isRMI){
            try{
                rmiClient.valid(message);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        else {
            Gson gson = new Gson();
            try {
                out.println(gson.toJson(new Response(Response.ResponseType.VALID, new ChatMessage(this.nickname, "",""), null, message)));
            } catch (Exception e) {
                System.out.println("Error occurred while sending a message: ");
                e.printStackTrace();
            }
        }
    }

    /**
     * method for notifying the client that a new one has joined the lobby
     * @param waitStatus
     */
    public synchronized void notifyNewJoin(String waitStatus) {
        if(isRMI){
            try{
                rmiClient.notifyNewJoin();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        else {
            Gson gson = new Gson();
            try {
                out.println(gson.toJson(new Response(Response.ResponseType.SOMEONE_JOINED_LOBBY, null, null, waitStatus)));
            } catch (Exception e) {
                System.out.println("Error occurred while sending a message: ");
                e.printStackTrace();
            }
        }
    }

    /**
     *
     * @throws IOException
     */
    public synchronized void sendPing() throws IOException{
        Gson gson = new Gson();
        out.println(gson.toJson(new Response(Response.ResponseType.PING, null,null, "ping")));
    }

    /**
     *
     * @return
     */
    public List<PingObject> getPongResponses() {
        return pongResponses;
    }



    public synchronized void sendShutdown(){
        if(isRMI){
            try{
                rmiClient.remoteShutdown("Closing...");
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        else {
            Gson gson = new Gson();
            out.println(gson.toJson(new Response(Response.ResponseType.SHUTDOWN, null, null, "Closing...")));
        }
    }

    public synchronized void sendView(View view) {
        if(isRMI){
            try{
                rmiClient.update(view);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        else {
            Gson gson = new Gson();
            out.println(gson.toJson(new Response(Response.ResponseType.UPDATE, null, view, null)));
        }
    }

    public RMIClient getRmiClient() {
        return rmiClient;
    }

    public synchronized void sendHelpMessage(){

        String string = "\t\t\t\t\t\t" + ANSI.BOLD + ANSI.ITALIC + "****** MY SHELFIE ******" + ANSI.RESET_STYLE + "\n\n" +
                ANSI.BOLD + "Command\t\t\tArguments\t\t\t\tNotes" + ANSI.RESET_STYLE + "\n" +
                "/chat\t\t\ttext message\t\t\tuse it to send a chat message to other players\n" +
                "/collect\t\tr1,c1 (r2,c2) (r3,c3)\t\tgame command to collect tiles from the board\n" +
                "/order\t\t\tC1 C2 C3\t\t\t\tuse it if you want change the tiles order before insert them in your shelf\n" +
                "/column\t\t\tcol number\t\t\t\tgame command to insert the tiles you have collected into your shelf at the selected column\n" +
                "/quit\t\t\t-\t\t\t\t\t\texit from the game\n";
        sendInfoMessage(string);
    }
    public void sendMenu(){
        if(isRMI){
            try{
                rmiClient.returnToMenu();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        else {
            Gson gson = new Gson();
            out.println(gson.toJson(new Response(Response.ResponseType.RETURN_TO_MENU, null, null, null)));
        }
    }

    public synchronized void setRmiClient(RMIClient client){
        this.rmiClient = client;
    }


    public void acceptLoadGame() {
        if(isRMI){
            try{
                rmiClient.acceptLoadGame();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        else {
            Gson gson = new Gson();
            out.println(gson.toJson(new Response(Response.ResponseType.ACCEPT_LOAD_GAME, null, null, null)));
        }
    }

    public void notifyCollectAccepted() {
        if(isRMI){
            try{
                rmiClient.acceptCollect();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        else {
            Gson gson = new Gson();
            out.println(gson.toJson(new Response(Response.ResponseType.ACCEPT_COLLECT, null, null, null)));
        }
    }

    public void notifyGameEnded(String rank) {
        if(isRMI){
            try{
                rmiClient.notifyGameEnded(rank);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        else {
            Gson gson = new Gson();
            out.println(gson.toJson(new Response(Response.ResponseType.GAME_ENDED, null, null, rank)));
        }
    }
}

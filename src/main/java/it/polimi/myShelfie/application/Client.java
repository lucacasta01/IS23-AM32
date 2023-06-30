package it.polimi.myShelfie.application;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import it.polimi.myShelfie.view.GUIcontroller.ChatController;
import it.polimi.myShelfie.view.GUIcontroller.GamePanelController;
import it.polimi.myShelfie.view.GUIcontroller.LoginController;
import it.polimi.myShelfie.view.GUIcontroller.banners.WaitPlayersController;
import it.polimi.myShelfie.network.RMI.RMIClient;
import it.polimi.myShelfie.network.RMI.RMIServer;
import it.polimi.myShelfie.network.inputHandlers.RMIInputHandler;
import it.polimi.myShelfie.network.inputHandlers.TCPInputHandler;
import it.polimi.myShelfie.utilities.*;
import it.polimi.myShelfie.utilities.pojo.Action;
import it.polimi.myShelfie.utilities.pojo.Response;
import it.polimi.myShelfie.view.View;
import it.polimi.myShelfie.utilities.pojo.ChatMessage;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.io.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * Text User Interface client.
 */
public class Client extends UnicastRemoteObject implements Runnable,RMIClient {
    private static Client instance;
    private BufferedReader in;
    private PrintWriter out;
    private Socket client;
    private String nickname = "/";
    private boolean done;
    private View view;
    private String connectionProtocol;
    private final List<PingObject> pongResponses = new ArrayList<>();
    private final RMIInputHandler RMIinputHandler;
    private final TCPInputHandler TCPinputHandler;
    private LoginController loginController;
    private WaitPlayersController waitPlayersController = null;
    private ChatController chatController;
    public String waitPlayerStatus;
    private String serverIP = Settings.SERVER_IP;
    private int TCPPort = Settings.TCPPORT;
    private int RMIPort = Settings.RMIPORT;
    //rmi server reference
    private RMIServer rmiServer;
    private boolean isGUI;
    private boolean notConnected = false;
    private GamePanelController gamePanelController = null;


    private Client() throws RemoteException {
        RMIinputHandler  = new RMIInputHandler(this);
        TCPinputHandler =  new TCPInputHandler(this);
        isGUI = false;
    }

    /**
     * getInstance method for singleton pattern
     * Creates a new client if instance is null
     * @return client instance
     */

    public static synchronized Client getInstance() {
        if(instance == null){
            try {
                System.out.println("created new client");
                instance = new Client();
            }catch(RemoteException e){
                e.printStackTrace();
                //todo
            }
        }
        return instance;
    }

    /**
     *
     * @return gui login panel controller
     */
    public LoginController getLoginController() {
        return loginController;
    }

    /**
     * set game panel gui controller
     * @param gamePanelController
     */
    public synchronized void setGamePanelController(GamePanelController gamePanelController) {
        this.gamePanelController = gamePanelController;
        this.notifyAll();
    }

    /**
     *
     * @return gui chat panel controller
     */
    public ChatController getChatController() {
        return chatController;
    }

    /**
     * Sets the chat panel gui controller
     * @param chatController
     */
    public void setChatController(ChatController chatController) {
        this.chatController = chatController;
    }

    /**
     * sets the gui wait for players panel controller
     * @param waitPlayersController
     */
    public void setWaitPlayersController(WaitPlayersController waitPlayersController) {
        this.waitPlayersController = waitPlayersController;
    }

    /**
     * sets gui login panel controller
     * @param loginController
     */
    public void setGuiLoginController(LoginController loginController) {
        this.loginController = loginController;
    }

    /**
     *
     * @return the server's ip address
     */
    public String getServerIP() {
        return serverIP;
    }

    /**
     *
     * @return true if the client is a gui client, false if it's a tui one
     */
    public boolean isGUI(){return isGUI;}

    /**
     *
     * @return TCP server port
     */
    public int getTCPPort() {
        return TCPPort;
    }
    /**
     *
     * @return RMI server port
     */
    public int getRMIPort() {
        return RMIPort;
    }

    /**
     * sets the server ip
     * @param serverIP
     */
    public void setServerIP(String serverIP) {
        this.serverIP = serverIP;
    }

    /**
     * sets TCP port
     * @param TCPPort
     */
    public void setTCPPort(int TCPPort) {
        this.TCPPort = TCPPort;
    }

    /**
     * sets RMI port
     * @param RMIPort
     */
    public void setRMIPort(int RMIPort) {
        this.RMIPort = RMIPort;
    }

    /**
     * sets the GUI boolean attribute in the client and in it's inputHandler
     * @param GUI
     */
    public void setGUI(boolean GUI) {
        isGUI = GUI;
        if(TCPinputHandler!=null){
            TCPinputHandler.setGUI(GUI);
        }
        if(RMIinputHandler!=null){
            RMIinputHandler.setGUI(GUI);
        }
    }




    @Override
    public void run() throws RuntimeException{
        boolean close = false;
        if (!isGUI) {
            connectionProtocol = protocolHandler();
            portHandler(connectionProtocol);
        }

        switch (connectionProtocol) {
            case "TCP" -> {


                try {
                    try {
                        client = new Socket(serverIP, TCPPort);
                    } catch (ConnectException connectException) {
                        System.out.println("Server not found");
                        if (in != null) {
                            try {
                                in.close();
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                        }
                        if (out != null) {
                            out.close();
                        }
                        if (isGUI) {
                            loginController.serverOffline();
                            loginController = null;
                        }
                        close = true;
                        notConnected = true;
                    }
                    if (!close) {
                        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                        out = new PrintWriter(client.getOutputStream(), true);


                        TCPinputHandler.start();

                        //PiNG THREAD
                        if (Settings.pingOn) {
                            pingThread().start();
                        }


                        new Thread(() -> {
                            String inMessage;
                            try {
                                while ((inMessage = in.readLine()) != null) {
                                    Response response = receiveResponse(inMessage);
                                    if (response.getResponseType() == Response.ResponseType.INFO) {
                                        System.out.println(response.getInfoMessage());
                                    } else if (response.getResponseType() == Response.ResponseType.CHATMESSAGE) {
                                        String sender = ANSI.BOLD + response.getChatMessage().getSenderColor() + response.getChatMessage().getSender() + ANSI.RESET_COLOR + ANSI.RESET_STYLE;
                                        System.out.println(">" + sender + ": " + response.getChatMessage().getMessage());
                                        if (isGUI) {
                                            gamePanelController.chatNotification();
                                            chatController.addMessage(response.getChatMessage());
                                        }
                                    } else if (response.getResponseType() == Response.ResponseType.VALID) {
                                        System.out.println(ANSI.GREEN + response.getInfoMessage() + ANSI.RESET_COLOR);
                                    } else if (response.getResponseType() == Response.ResponseType.NICKNAME_ACCEPTED) {
                                        nickname = response.getChatMessage().getSender();
                                        if (isGUI) {
                                            loginController.loginAccepted();
                                        }
                                    } else if (response.getResponseType() == Response.ResponseType.DENIED) {
                                        System.out.println(response.getInfoMessage());

                                    } else if (response.getResponseType() == Response.ResponseType.NICKNAME_DENIED) {
                                        if (isGUI) {
                                            loginController.nicknameDenied(response.getInfoMessage());
                                        }
                                    } else if (response.getResponseType() == Response.ResponseType.GAME_STARTED) {
                                        if (isGUI) {
                                            GUIClient.getInstance().switchToGame();
                                        }
                                    } else if (response.getResponseType() == Response.ResponseType.GAME_ENDED) {
                                        if (isGUI) {
                                            GUIClient.getInstance().showRank(response.getInfoMessage());
                                        } else {
                                            System.out.println("\n(1) New Game");
                                            System.out.println("(2) Load last game");
                                            System.out.println("(3) Join random game");
                                            System.out.println("(4) Search for started saved game");
                                            System.out.println("(0) Exit\n");
                                        }
                                    } else if (response.getResponseType() == Response.ResponseType.UPDATE) {
                                        view = response.getView();
                                        if (isGUI) {
                                            while (gamePanelController == null) {
                                                synchronized (this) {
                                                    wait();
                                                }
                                            }
                                            gamePanelController.updateView();
                                        }

                                        Utils.printTUIView(view);
                                    } else if (response.getResponseType() == Response.ResponseType.PONG) {
                                        synchronized (pongResponses) {
                                            pongResponses.add(new PingObject(false));
                                            pongResponses.notifyAll();
                                        }

                                    } else if (response.getResponseType() == Response.ResponseType.PING) {
                                        sendAction(new Action(Action.ActionType.PONG, nickname, null, null, null, null));

                                    } else if (response.getResponseType() == Response.ResponseType.SHUTDOWN) {
                                        System.out.println(response.getInfoMessage());
                                        shutdown();
                                    } else if (response.getResponseType() == Response.ResponseType.LOBBY_CREATED) {
                                        synchronized (this) {
                                            waitPlayerStatus = "(1/" + response.getInfoMessage() + ")";
                                        }
                                    } else if (response.getResponseType() == Response.ResponseType.LOBBY_JOINED) {
                                        System.out.println("Lobby joined");
                                        setWaitPlayerStatus(response.getInfoMessage());
                                        System.out.println("Waiting for players..." + getWaitPlayerStatus());
                                        if (isGUI) {
                                            GUIClient.getInstance().switchToWaitingScene();
                                        }
                                    } else if (response.getResponseType() == Response.ResponseType.SOMEONE_JOINED_LOBBY) {
                                        if (isGUI) {
                                            waitPlayerStatus = response.getInfoMessage();
                                            waitPlayersController.updateLabel("Waiting for players..." + waitPlayerStatus);
                                        }
                                    } else if (response.getResponseType() == Response.ResponseType.DENY_LOAD_GAME) {
                                        if (isGUI) {
                                            GUIClient.getInstance().showDenyDialog("NO OLD GAME FOUND");
                                        }
                                    } else if (response.getResponseType() == Response.ResponseType.RANDOM_GAME_NOT_FOUND) {
                                        if(isGUI) {
                                            GUIClient.getInstance().showDenyDialog("No game available\nYou should create a new one".toUpperCase());
                                        }
                                    } else if (response.getResponseType() == Response.ResponseType.ACCEPT_LOAD_GAME) {
                                        if (isGUI) {
                                            GUIClient.getInstance().switchToWaitingScene();
                                        }
                                    } else if (response.getResponseType() == Response.ResponseType.ACCEPT_COLLECT) {
                                        if (isGUI) {
                                            gamePanelController.insertInColumn();
                                        }
                                    } else if (response.getResponseType() == Response.ResponseType.RETURN_TO_MENU) {
                                        if (isGUI) {
                                            GUIClient.getInstance().switchToMenu();
                                            gamePanelController = null;
                                        } else {
                                            System.out.println("\n(1) New Game");
                                            System.out.println("(2) Load last game");
                                            System.out.println("(3) Join random game");
                                            System.out.println("(4) Search for started saved game");
                                            System.out.println("(0) Exit\n");
                                        }
                                    } else if (response.getResponseType() == Response.ResponseType.FOUND_OLD_GAME) {
                                        if (isGUI) {
                                            GUIClient.getInstance().showOldGameChoice();
                                        }
                                    } else if (response.getResponseType() == Response.ResponseType.OLD_GAME_NOT_FOUND) {
                                        if (isGUI) {
                                            GUIClient.getInstance().showDenyDialog("NO STARTED OLD GAME FOUND");
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                //todo (when null or close exit)
                            }

                        }).start();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            case "RMI" -> {
                try {
                    startRMIClient();
                } catch (RemoteException | NotBoundException e) {
                    System.err.println("Server not found");
                    if (isGUI) {
                        try {
                            loginController.serverOffline();
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }

                        loginController = null;
                        close = true;
                    }
                    notConnected = true;
                }
                if (!close) {
                    //PING THREAD
                    if (Settings.pingOn) {
                        pingThread().start();
                    }

                    RMIinputHandler.start();
                }
            }
            default -> System.exit(11);
        }
    }

    /**
     *
     * @return waitPlayer status
     */
    public synchronized String getWaitPlayerStatus() {
        return waitPlayerStatus;
    }

    /**
     * sets the waitPlayers status
     * @param waitPlayerStatus
     */
    public void setWaitPlayerStatus(String waitPlayerStatus) {
        this.waitPlayerStatus = waitPlayerStatus;
    }

    /**
     *
     * @return RMI server interface
     */
    public RMIServer getRmiServer() {
        return rmiServer;
    }

    /**
     * set the client's connection protocol
     * @param connectionProtocol
     */
    public void setConnectionProtocol(String connectionProtocol) {
        this.connectionProtocol = connectionProtocol;
    }

    /**
     *
     * @return client's done boolean
     */
    public boolean getDone(){
        return this.done;
    }

    /**
     *
     * @return client's nickname
     */
    public String getNickname(){
        return this.nickname;
    }

    /**
     * bind this client to the rmi server if online
     * @throws RemoteException
     * @throws NotBoundException
     */
    private void startRMIClient() throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry(serverIP,RMIPort);
        this.rmiServer = (RMIServer)registry.lookup(Settings.RMINAME);
        rmiServer.addClient(this);
    }

    /**
     * CLoses this client's process
     */
    public void shutdown() {
        done = true;
        System.exit(0);
    }

    /**
     * Ping thread method, used by the client to ping the server
     * @return a new ping thread
     */
    public Thread pingThread() {

        return new Thread(() -> {
            switch (connectionProtocol) {
                case "TCP" -> {
                    int failedPings = 0;
                    int count = 1;
                    while (true) {
                        try {
                            sendAction(new Action(Action.ActionType.PING, nickname, null, Integer.toString(count), null, null));
                        } catch (IOException e) {
                            System.exit(10);
                        }

                        SwapElapsed swapElapsed = new SwapElapsed();
                        swapElapsed.start();


                        while (pongResponses.size() == 0) {
                            synchronized (pongResponses) {
                                try {
                                    pongResponses.wait();
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }


                        if (pongResponses.get(0).isElapsed()) {
                            failedPings++;
                            System.out.println("Ping #" + count + " elapsed");
                            if (failedPings > 2) {
                                System.out.println("Server offline: closing...");
                                if(!isGUI) {
                                    System.exit(1);
                                }else{
                                    GUIClient.getInstance().showServerOfflineBan();
                                }
                            }
                            synchronized (pongResponses) {
                                pongResponses.remove(0);
                            }
                        } else {
                            try {
                                swapElapsed.setRunning(false);
                            } catch (Exception e) {
                                //ignore
                            }
                            synchronized (pongResponses) {
                                pongResponses.remove(0);
                            }
                            try {
                                Thread.sleep(Settings.PINGPERIOD);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            count++;
                        }
                    }
                }
                case "RMI" -> {
                    boolean pingFailed = false;
                    while (!pingFailed) {
                        try {
                            rmiServer.ping();
                        } catch (RemoteException e) { //ping failed
                            pingFailed = true;
                            System.err.println("Server offline: closing...");
                            if(!isGUI) {
                                System.exit(1);
                            }else{
                                GUIClient.getInstance().showServerOfflineBan();
                            }
                        }
                        try {
                            Thread.sleep(Settings.PINGPERIOD);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    public static void main(String[] args) throws RemoteException {
        Client client = Client.getInstance();
        client.setGUI(false);
        try {
            client.run();
        } catch (Exception e) {
            System.out.println("Closing");
            System.exit(0);
        }
        if(!client.isGUI&& client.notConnected){
            System.exit(0);
        }
    }
    private String protocolHandler(){
            String message;
            BufferedReader inReader = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Choose your protocol [TCP/RMI]");
            try {
                message= inReader.readLine().toUpperCase();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            while(!message.equalsIgnoreCase("RMI")&&!message.equalsIgnoreCase("TCP")){
                System.out.println("Type the right key...");
                try {
                    message= inReader.readLine().toUpperCase();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return message;
    }

    private void portHandler(String connectionProtocol){
        String message;
        BufferedReader inReader = new BufferedReader(new InputStreamReader(System.in));

        if(connectionProtocol.equals("RMI")){
            System.out.println("RMI port: "+RMIPort);
        }else{
            System.out.println("TCP port: "+TCPPort);
        }
        System.out.println("Would you like to change the port? [y/n]");
        try {
            message= inReader.readLine().toUpperCase();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        while(!message.equalsIgnoreCase("Y")&&!message.equalsIgnoreCase("N")){
            System.out.println("Type the right key...");
            try {
                message= inReader.readLine().toUpperCase();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        if(message.equalsIgnoreCase("Y")){
            System.out.println("Port number: ");
            try {
                message= inReader.readLine().toUpperCase();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            while(!message.matches("^\\d{1,5}$")){
                System.out.println("Type the right key...");
                try {
                    message= inReader.readLine().toUpperCase();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            if(connectionProtocol.equals("RMI")){
                this.RMIPort = Integer.parseInt(message);
            }else{
                this.TCPPort = Integer.parseInt(message);
            }
        }
    }

    /**
     *
     * @param jString jString recieved from the server
     * @return a response from the server, obtained by a json string
     * @throws JsonSyntaxException
     */
    public Response receiveResponse(String jString) throws JsonSyntaxException {
        return JsonParser.getResponse(jString);
    }

    /**
     * Send a json string that contains an action to the server
     * @param action to send
     * @throws IOException
     */
    public synchronized void sendAction(Action action) throws IOException {
        Gson gson = new Gson();
        if (out != null) {
            out.println(gson.toJson(action));
        }
    }

    /**
     * set this client's nickname
     * @param nickname
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }


    //RMIClient interface methods implementation

    /**
     * Server calls this method when the nickname is  accepted (login ok)
     * @throws RemoteException
     */
    @Override
    public void nicknameAccepted() throws RemoteException {
        if(isGUI){
            try {
                loginController.loginAccepted();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * Server calls this method when the nickname is  denied (login not ok)
     * @param message string used to describe the error
     * @throws RemoteException
     */
    @Override
    public void nicknameDenied(String message) throws RemoteException {
        if(isGUI){
            try {
                loginController.nicknameDenied(message);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        else{
            System.err.println(message);
        }
    }

    /**
     * RMI ping method
     * @throws RemoteException
     */
    @Override
    public void ping() throws RemoteException {
    }


    /**
     * Notify to the client that his game is started
     * @throws RemoteException
     */
    @Override
    public void notifyGameStarted() throws RemoteException {
        if(isGUI) {
            GUIClient.getInstance().switchToGame();
        }
    }

    /**
     * Updates rmi view when a new view is sent from the server
     * @param view
     * @throws RemoteException
     */
    @Override
    public void update(View view) throws RemoteException {
        Client.getInstance().view=view;
        Utils.printTUIView(view);
        if(isGUI){
            System.out.println("GUI view update");
            while(gamePanelController == null){
                System.out.println("gamePanelController is null");
                synchronized (this){
                    try{
                        wait();
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            }
            System.out.println("Updating view finally...");
            gamePanelController.updateView();
        }
    }

    /**
     * Receives a chat message from the server
     * @param chatMessage
     * @throws RemoteException
     */
    @Override
    public void chatMessage(ChatMessage chatMessage) throws RemoteException {
        String sender = ANSI.BOLD + chatMessage.getSenderColor() +chatMessage.getSender() + ANSI.RESET_COLOR + ANSI.RESET_STYLE;
        System.out.println(">" + sender + ": " + chatMessage.getMessage());
        if(isGUI){
            gamePanelController.chatNotification();
            Client.getInstance().getChatController().addMessage(chatMessage);
        }
    }

    /**
     * notify the client that the lobby creation went good
     * @param lobbySize
     * @throws RemoteException
     */
    @Override
    public void notifyLobbyCreated(String lobbySize) throws RemoteException {
        waitPlayerStatus = "(1/"+lobbySize+")";
        System.out.println("Lobby created");
    }

    /**
     * notify the client that he joined a game
     * @throws RemoteException
     */
    @Override
    public void notifyGameJoined() throws RemoteException {
        if(isGUI) {
            GUIClient guiClient = GUIClient.getInstance();
            Stage stage = guiClient.getStage();
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    Parent numberofplayer;
                    try {
                        numberofplayer = FXMLLoader.load(getClass().getResource("/waitPlayerBanner.fxml"));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    stage.setMinWidth(300);
                    stage.setMinHeight(150);
                    stage.setWidth(300);
                    stage.setHeight(150);
                    stage.setScene(new Scene(numberofplayer));
                }
            });
        }
    }

    /**
     * generic valid message from the server
     * @param message
     * @throws RemoteException
     */
    @Override
    public void valid(String message) throws RemoteException {
        System.out.println(ANSI.GREEN+message+ANSI.RESET_COLOR);
    }

    /**
     * generic denied message from the server
     * @param message
     * @throws RemoteException
     */
    @Override
    public void denied(String message) throws RemoteException {
        if(message.startsWith("Username")){
            try {
                loginController.nicknameDenied(message);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        System.err.println(message);
    }

    /**
     * generic info message from the server
     * @param message
     * @throws RemoteException
     */
    @Override
    public void infoMessage(String message) throws RemoteException {
        System.out.println(message);
    }

    /**
     * Deny the attempt to load an old game
     * @throws RemoteException
     */
    @Override
    public void denyLoadGame() throws RemoteException {
        if(isGUI) {
            GUIClient.getInstance().showDenyDialog("NO OLD GAME FOUND");
        }
    }

    /**
     * accepts the attempt to load an old game
     * @throws RemoteException
     */
    @Override
    public void acceptLoadGame() throws RemoteException {
        if(isGUI) {
            GUIClient.getInstance().switchToWaitingScene();
        }
    }

    /**
     * used when the client needs to be turned off by the server
     * @param message
     * @throws RemoteException
     */
    @Override
    public void remoteShutdown(String message) throws RemoteException {
        System.out.println(message);
        new Thread(()->{
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            shutdown();
        }).start();

    }

    /**
     * notify this client that the match is over
     * @param rank
     * @throws RemoteException
     */
    @Override
    public void notifyGameEnded(String rank) throws RemoteException {
        if(isGUI){
            GUIClient.getInstance().showRank(rank);
        }
    }

    /**
     * notify this client that someone joined his lobby
     * @throws RemoteException
     */
    @Override
    public void notifyNewJoin() throws RemoteException {
        //todo
    }

    /**
     * return to the menu page (view)
     * @throws RemoteException
     */
    @Override
    public void returnToMenu() throws RemoteException {
        if(isGUI) {
            GUIClient.getInstance().switchToMenu();
        }else{
            System.out.println("\n(1) New Game");
            System.out.println("(2) Load last game");
            System.out.println("(3) Join random game");
            System.out.println("(4) Search for started saved game");
            System.out.println("(0) Exit\n");
        }
    }

    /**
     * notify an old game found
     * @throws RemoteException
     */
    @Override
    public void foundOldGame() throws RemoteException {
        if(isGUI) {
            GUIClient.getInstance().showOldGameChoice();
        }
    }

    /**
     * notify old game not found
     * @throws RemoteException
     */
    @Override
    public void oldGameNotFound() throws RemoteException {
        if(isGUI) {
            GUIClient.getInstance().showDenyDialog("NO STARTED OLD GAME FOUND");
        }
    }

    /**
     * deny access to a random game
     * @throws RemoteException
     */
    @Override
    public void denyRandomGame() throws RemoteException {
        if(isGUI) {
            GUIClient.getInstance().showDenyDialog("No game available\nYou should create a new one".toUpperCase());
        }
    }

    /**
     * accept collected tiles
     * @throws RemoteException
     */
    @Override
    public void acceptCollect() throws RemoteException {
        if(isGUI) {
            gamePanelController.insertInColumn();
        }
    }

    /**
     * adds a gui action that needs to be sent to the server
     * @param action
     */
    public void addGuiAction(String action){
        if(Objects.equals(connectionProtocol, "TCP")) {
            TCPinputHandler.addGuiAction(action);
        }else{
            RMIinputHandler.addGuiAction((action));
        }
    }

    /**
     * returns this client's view (last received from the server)
     * @return
     */
    public View getView() {
        return view;
    }
    class SwapElapsed extends Thread {
        private boolean isRunning = true;

        public boolean isRunning() {
            return isRunning;
        }

        public void setRunning(boolean running) {
            isRunning = running;
        }

        @Override
        public void run() {
            int time = 0;
            while (isRunning() && time < Settings.PINGTHRESHOLD) {
                try {
                    Thread.sleep(Settings.PINGTHRESHOLD/ Settings.PINGFACTOR);
                    time += Settings.PINGTHRESHOLD/ Settings.PINGFACTOR;
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            if (isRunning()) {
                synchronized (pongResponses) {
                    pongResponses.add(new PingObject(true));
                    pongResponses.notifyAll();
                }
            }
        }
    }
}



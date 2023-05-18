package it.polimi.myShelfie.application;
import com.google.gson.Gson;
import it.polimi.myShelfie.controller.GUIcontroller.LoginController;
import it.polimi.myShelfie.controller.GUIcontroller.banners.WaitPlayersController;
import it.polimi.myShelfie.controller.RMI.RMIClient;
import it.polimi.myShelfie.controller.RMI.RMIServer;
import it.polimi.myShelfie.controller.inputHandlers.RMIInputHandler;
import it.polimi.myShelfie.controller.inputHandlers.TCPInputHandler;
import it.polimi.myShelfie.utilities.ANSI;
import it.polimi.myShelfie.utilities.Settings;
import it.polimi.myShelfie.utilities.JsonParser;
import it.polimi.myShelfie.utilities.PingObject;
import it.polimi.myShelfie.utilities.beans.Action;
import it.polimi.myShelfie.utilities.beans.Response;
import it.polimi.myShelfie.utilities.beans.View;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.io.*;
import java.nio.file.Paths;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Client extends UnicastRemoteObject implements Runnable,RMIClient {
    private static Client instance;
    private BufferedReader in;
    private PrintWriter out;
    private Socket client;
    private String nickname = "/";
    private boolean done;
    private boolean configurationDone = false;
    private View view;
    private String connectionProtocol;
    private final List<PingObject> pongResponses = new ArrayList<>();
    private final RMIInputHandler RMIinputHandler;
    private final TCPInputHandler TCPinputHandler;
    private LoginController loginController;
    private WaitPlayersController waitPlayersController = null;
    public String waitPlayerStatus;
    private String serverIP = Settings.SERVER_IP;
    private int TCPPort = Settings.TCPPORT;
    private int RMIPort = Settings.RMIPORT;
    //rmi server reference
    private RMIServer rmiServer;
    private boolean isGUI = false;
    private final List<Response> guiResponses = new ArrayList<>();
    private boolean notConnected = false;


    private Client() throws RemoteException {
        RMIinputHandler  = new RMIInputHandler(this);
        TCPinputHandler =  new TCPInputHandler(this);
        isGUI = false;
    }

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

    public LoginController getLoginController() {
        return loginController;
    }

    public String getServerIP() {
        return serverIP;
    }

    public int getTCPPort() {
        return TCPPort;
    }

    public int getRMIPort() {
        return RMIPort;
    }

    public void setServerIP(String serverIP) {
        this.serverIP = serverIP;
    }

    public void setTCPPort(int TCPPort) {
        this.TCPPort = TCPPort;
    }

    public void setRMIPort(int RMIPort) {
        this.RMIPort = RMIPort;
    }

    public void setGUI(boolean GUI) {
        isGUI = GUI;
        if(connectionProtocol=="TCP"){
            TCPinputHandler.setGUI(GUI);
        }else{
            RMIinputHandler.setGUI(GUI);
        }
    }

    public void setWaitPlayersController(WaitPlayersController waitPlayersController) {
        this.waitPlayersController = waitPlayersController;
    }

    @Override
    public void run() throws RuntimeException{
        boolean close = false;
        if (!isGUI) {
            connectionProtocol = protocolHandler();
        }

        switch (connectionProtocol) {
            case "TCP":
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

                        //PING THREAD
                        if (Settings.pingOn) {
                            //pingThread().start();
                        }


                        new Thread(() -> {
                            String inMessage;
                            try {
                                while ((inMessage = in.readLine()) != null) {
                                    Response response = recieveResponse(inMessage);
                                    if (response.getResponseType() == Response.ResponseType.INFO) {
                                        System.out.println(response.getInfoMessage());
                                    } else if (response.getResponseType() == Response.ResponseType.CHATMESSAGE) {
                                        System.out.println(">" + response.getChatMessage().getSender() + ": " + response.getChatMessage().getMessage());
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
                                    } else if (response.getResponseType() == Response.ResponseType.UPDATE) {
                                        view = response.getView();
                                        //shelves
                                        for (String s : view.getShelves()) {
                                            System.out.println(s + "\n");
                                        }
                                        //personal card
                                        System.out.println(ANSI.ITALIQUE + "Personal goal card:" + ANSI.RESET_STYLE);
                                        System.out.println(view.getPersonalCard());

                                        //shared cards
                                        for (int i = 0; i < view.getSharedCards().size(); i++) {
                                            System.out.println(ANSI.ITALIQUE + "Shared goal " + (i + 1) + ": " + ANSI.RESET_STYLE);
                                            System.out.println(view.getSharedCards().get(i) + "\n");
                                        }
                                        //board
                                        System.out.println(ANSI.ITALIQUE + "Board:" + ANSI.RESET_STYLE);
                                        System.out.println(view.getBoard() + "\n");
                                        //current player
                                        System.out.println(ANSI.ITALIQUE + "Turn of: " + ANSI.RESET_STYLE + view.getCurrentPlayer());
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
                                            GUIClient.getInstance().showDenyBan();
                                        }
                                    } else if (response.getResponseType() == Response.ResponseType.ACCEPT_LOAD_GAME) {
                                        if (isGUI) {
                                            GUIClient.getInstance().switchToWaitingScene();
                                        }
                                    } else if (response.getResponseType() == Response.ResponseType.RETURN_TO_MENU) {
                                        if (isGUI) {
                                            GUIClient.getInstance().switchToMenu();
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
                                            GUIClient.getInstance().showOldGameNotFound();
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
                break;
            case "RMI":
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
                if(!close) {
                    BufferedReader inReader = new BufferedReader(new InputStreamReader(System.in));
                    //PING THREAD
                    if (Settings.pingOn) {
                        pingThread().start();
                    }

                    RMIinputHandler.start();
                }
                break;
            default:
                System.exit(11);
        }
    }


    public synchronized String getWaitPlayerStatus() {
        return waitPlayerStatus;
    }

    public void setWaitPlayerStatus(String waitPlayerStatus) {
        this.waitPlayerStatus = waitPlayerStatus;
    }

    public RMIServer getRmiServer() {
        return rmiServer;
    }

    public void setGuiLoginController(LoginController loginController) {
        this.loginController = loginController;
    }

    public String getConnectionProtocol() {
        return connectionProtocol;
    }

    public void setConnectionProtocol(String connectionProtocol) {
        this.connectionProtocol = connectionProtocol;
    }

    public boolean getDone(){
        return this.done;
    }
    public String getNickname(){
        return this.nickname;
    }

    private void startRMIClient() throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry(serverIP,RMIPort);
        this.rmiServer = (RMIServer)registry.lookup(Settings.RMINAME);
        rmiServer.addClient(this);
    }
    public void shutdown() {
        done = true;
        if(connectionProtocol.equals("TCP")) {
            try {
                in.close();
                out.close();
                if (!client.isClosed()){
                    client.close();
                }
                System.exit(0);
            } catch (Exception e) {
                System.out.println(e.toString());
            }
        }else{
            System.exit(0);
        }
    }

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
                                System.exit(1);
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
                            System.exit(1);
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

    public Response recieveResponse(String jString) throws IOException {
        return JsonParser.getResponse(jString);
    }

    public synchronized void sendAction(Action action) throws IOException {
        Gson gson = new Gson();
        if (out != null) {
            out.println(gson.toJson(action));
        }
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    //RMIClient interface implementation


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
    @Override
    public void ping() throws RemoteException {

    }
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

    @Override
    public void notifyGameStarted() throws RemoteException {
        if(isGUI) {
            GUIClient.getInstance().switchToGame();
        }
    }

    @Override
    public void update(View view) throws RemoteException {
        //shelves
        for (String s : view.getShelves()) {
            System.out.println(s + "\n");
        }
        //personal card
        System.out.println(ANSI.ITALIQUE + "Personal goal card:" + ANSI.RESET_STYLE);
        System.out.println(view.getPersonalCard());

        //shared cards
        for (int i = 0; i < view.getSharedCards().size(); i++) {
            System.out.println(ANSI.ITALIQUE + "Shared goal " + (i + 1) + ": " + ANSI.RESET_STYLE);
            System.out.println(view.getSharedCards().get(i) + "\n");
        }
        //board
        System.out.println(ANSI.ITALIQUE + "Board:" + ANSI.RESET_STYLE);
        System.out.println(view.getBoard() + "\n");
        //current player
        System.out.println(ANSI.ITALIQUE + "Turn of: " + ANSI.RESET_STYLE + view.getCurrentPlayer());
    }

    @Override
    public void chatMessage(String sender, String message) throws RemoteException {
        System.out.println(">" + sender + ": " + message);
    }

    @Override
    public void notifyLobbyCreated(String lobbySize) throws RemoteException {
        waitPlayerStatus = "(1/"+lobbySize+")";
        System.out.println("Lobby created");
    }

    @Override
    public void notifyGameJoined() throws RemoteException {
        if(isGUI) {
            GUIClient guiClient = GUIClient.getInstance();
            Stage stage = guiClient.getStage();
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    Parent numbeofplayer = null;
                    try {
                        numbeofplayer = FXMLLoader.load(Paths.get("src/resources/waitPlayerBanner.fxml").toUri().toURL());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    stage.setMinWidth(300);
                    stage.setMinHeight(150);
                    stage.setWidth(300);
                    stage.setHeight(150);
                    stage.setScene(new Scene(numbeofplayer));
                }
            });
        }
    }

    @Override
    public void valid(String message) throws RemoteException {
        System.out.println(ANSI.GREEN+message+ANSI.RESET_COLOR);
    }

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

    @Override
    public void infoMessage(String message) throws RemoteException {
        System.out.println(message);
    }

    @Override
    public void denyLoadGame() throws RemoteException {
        if(isGUI) {
            GUIClient.getInstance().showDenyBan();
        }
    }

    @Override
    public void acceptLoadGame() throws RemoteException {
        if(isGUI) {
            GUIClient.getInstance().switchToWaitingScene();
        }
    }

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

    @Override
    public void notifyNewJoin() throws RemoteException {
        //todo
    }

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

    @Override
    public void foundOldGame() throws RemoteException {
        if(isGUI) {
            GUIClient.getInstance().showOldGameChoice();
        }
    }

    @Override
    public void oldGameNotFound() throws RemoteException {
        GUIClient.getInstance().showOldGameNotFound();
    }

    public void addGuiAction(String action){
        if(Objects.equals(connectionProtocol, "TCP")) {
            TCPinputHandler.addGuiAction(action);
        }else{
            RMIinputHandler.addGuiAction((action));
        }
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



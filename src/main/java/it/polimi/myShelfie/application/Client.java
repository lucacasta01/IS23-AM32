package it.polimi.myShelfie.application;
import com.google.gson.Gson;
import it.polimi.myShelfie.application.controller.GUIController;
import it.polimi.myShelfie.controller.RMI.RMIClient;
import it.polimi.myShelfie.controller.RMI.RMIServer;
import it.polimi.myShelfie.controller.inputHandlers.RMIInputHandler;
import it.polimi.myShelfie.controller.inputHandlers.TCPInputHandler;
import it.polimi.myShelfie.utilities.ANSI;
import it.polimi.myShelfie.utilities.Constants;
import it.polimi.myShelfie.utilities.JsonParser;
import it.polimi.myShelfie.utilities.PingObject;
import it.polimi.myShelfie.utilities.beans.Action;
import it.polimi.myShelfie.utilities.beans.Response;
import it.polimi.myShelfie.utilities.beans.View;
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
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class Client extends UnicastRemoteObject implements Runnable,RMIClient {

    private BufferedReader in;
    private PrintWriter out;
    private Socket client;
    private String nickname = "/";
    private boolean done;
    private boolean configurationDone = false;
    private boolean validRecieved = false;
    private View view;
    private String connectionProtocol;
    private final List<PingObject> pongResponses = new ArrayList<>();
    private final RMIInputHandler RMIinputHandler;
    private final TCPInputHandler TCPinputHandler;
    private GUIController guiController;

    //rmi server reference
    private RMIServer rmiServer;
    private boolean isGUI = false;
    private final List<Response> guiResponses = new ArrayList<>();



    public Client(boolean GUI) throws RemoteException {
        RMIinputHandler  = new RMIInputHandler(this);
        TCPinputHandler =  new TCPInputHandler(this,GUI);
        isGUI = GUI;
    }

    @Override
    public void run() {
        if(!isGUI) {
            connectionProtocol = protocolHandler();
        }

        switch (connectionProtocol){
            case "TCP":
                try {
                    try {
                        client = new Socket(Constants.SERVER_IP, Constants.PORT);

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
                        throw new RuntimeException();
                    }
                    in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    out = new PrintWriter(client.getOutputStream(), true);


                    TCPinputHandler.start();

                    //PING THREAD
                    pingThread().start();


                    new Thread(()->{
                        String inMessage;
                        try{
                            while ((inMessage = in.readLine()) != null) {
                                Response response = recieveResponse(inMessage);
                                if (response.getResponseType() == Response.ResponseType.INFO) {
                                    System.out.println(response.getInfoMessage());
                                } else if (response.getResponseType() == Response.ResponseType.CHATMESSAGE) {
                                    System.out.println(">" + response.getChatMessage().getSender() + ": " + response.getChatMessage().getMessage());
                                } else if (response.getResponseType() == Response.ResponseType.VALID) {
                                    validRecieved = true;
                                    if (response.getInfoMessage().equals("Username accepted")) {
                                        nickname = response.getChatMessage().getSender();
                                    }
                                    System.out.println(ANSI.GREEN + response.getInfoMessage() + ANSI.RESET_COLOR);
                                    if(isGUI){
                                        guiController.loginAccepted();
                                    }
                                } else if (response.getResponseType() == Response.ResponseType.DENIED) {
                                    System.out.println(response.getInfoMessage());
                                    if(isGUI){
                                        guiController.nicknameDenied();
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
                                }
                            }
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }).start();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case "RMI":
                try {
                    startRMIClient();
                } catch (RemoteException | NotBoundException e) {
                    System.err.println("Server not found");
                    System.out.println("Closing...");
                    System.exit(1);
                }
                BufferedReader inReader = new BufferedReader(new InputStreamReader(System.in));
                //PING THREAD
                pingThread().start();
                try {
                    nickname = inReader.readLine();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                try {
                    while (!rmiServer.login(nickname, this)){
                        System.out.println("Nickname already use, retry:");
                        nickname = inReader.readLine();
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }

                RMIinputHandler.start();

                break;
            default:
                System.exit(11);
        }
    }

    public RMIServer getRmiServer() {
        return rmiServer;
    }

    public void setGuiController(GUIController guiController) {
        this.guiController = guiController;
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
        Registry registry = LocateRegistry.getRegistry(Constants.SERVER_IP,Constants.RMIPORT);
        this.rmiServer = (RMIServer)registry.lookup(Constants.RMINAME);
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
                                Thread.sleep(Constants.PINGPERIOD);
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
                            Thread.sleep(Constants.PINGPERIOD);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    @Override
    public void ping() throws RemoteException {

    }

    public static void main(String[] args) throws RemoteException {
        Client client = new Client(false);
        try {
            client.run();
        } catch (RuntimeException e) {
            System.out.println("Closing");
            System.exit(0);
        }

    }
    private String protocolHandler(){
            String message;
            BufferedReader inReader = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Choose your protocol [TCP/RMI]");
            try {
                message= inReader.readLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            while(!message.toUpperCase().equals("RMI")&&!message.toUpperCase().equals("TCP")){
                System.out.println("Type the right key...");
                try {
                    message= inReader.readLine();
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

    //RMIClient interface implementation


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
    public void valid(String message) throws RemoteException {
        System.out.println(ANSI.GREEN+message+ANSI.RESET_COLOR);
    }

    @Override
    public void denied(String message) throws RemoteException {
        System.err.println(message);
    }

    @Override
    public void infoMessage(String message) throws RemoteException {
        System.out.println(message);
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

    public void addGuiAction(String action){
        TCPinputHandler.addGuiAction(action);
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
            while (isRunning() && time < Constants.PINGTHRESHOLD) {
                try {
                    Thread.sleep(Constants.PINGTHRESHOLD/Constants.PINGFACTOR);
                    time += Constants.PINGTHRESHOLD/Constants.PINGFACTOR;
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



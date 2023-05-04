package it.polimi.myShelfie.application;
import com.google.gson.Gson;
import it.polimi.myShelfie.controller.ClientHandler;
import it.polimi.myShelfie.controller.RMI.RMIClient;
import it.polimi.myShelfie.controller.RMI.RMIServer;
import it.polimi.myShelfie.model.Position;
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
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class Client extends UnicastRemoteObject implements Runnable,RMIClient {

    private BufferedReader in;
    private PrintWriter out;
    private Socket client;
    private String nickname;
    private boolean done;
    private boolean configurationDone = false;
    private Response response;
    private boolean validRecieved = false;
    private View view;
    private final List<PingObject> pongResponses = new ArrayList<>();

    //rmi server reference
    RMIServer rmiServer;

    protected Client() throws RemoteException {
    }

    @Override
    public void run() {
        String connectionProtocol = protocolHandler();
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

                    InputHandler inHandler = new InputHandler();
                    Thread t = new Thread(inHandler);
                    t.start();

                    //PING THREAD
                    pingThread().start();

                    String inMessage;

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
                            System.out.println(response.getInfoMessage());
                        } else if (response.getResponseType() == Response.ResponseType.DENIED) {
                            System.out.println(response.getInfoMessage());
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
                } catch (Exception e) {
                    throw new RuntimeException();
                }
                break;
            case "RMI":
                try {
                    startRMIClient();
                } catch (RemoteException | NotBoundException e) {
                    e.printStackTrace();
                }

                String nickname;
                BufferedReader inReader = new BufferedReader(new InputStreamReader(System.in));


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

                new Thread(new RMIInputHandler()).start();

                break;
            default:
                System.exit(11);
        }
    }

    private void startRMIClient() throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry(Constants.SERVER_IP,Constants.RMIPORT);
        this.rmiServer = (RMIServer)registry.lookup(Constants.RMINAME);
        rmiServer.addClient(this);
    }
    public void shutdown() {
        done = true;

            try {
                in.close();
                out.close();
                if (!client.isClosed()) {
                    client.close();
                }
                System.exit(0);
            } catch (Exception e) {
                System.out.println(e.toString());
            }


    }

    public Thread pingThread() {

        return new Thread(() -> {
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
                    System.out.println("Server offline: closing...");
                    System.exit(1);

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
        });
    }


    public static void main(String[] args) throws RemoteException {
        Client client = new Client();
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

    class InputHandler implements Runnable {

        public void run() {
            String message;
            BufferedReader inReader = new BufferedReader(new InputStreamReader(System.in));
            while (!done) {
                try {
                    message = inReader.readLine();

                    if (message.equals("/quit")) {
                        Action a = new Action(Action.ActionType.QUIT, nickname, "", "", null, null);
                        sendAction(a);
                        inReader.close();
                        shutdown();
                    } else if (message.startsWith("/chat")) {
                        Action a = new Action(Action.ActionType.CHAT, nickname, message.substring(message.indexOf("/chat") + "/chat ".length()), "", null, null);
                        sendAction(a);
                    }
                    /*
                     * /collect x1,y1 (opt)x2,y2 (opt)x3,y3
                     */
                    else if (message.startsWith("/collect")) {
                        int firstTile = "/collect ".length();
                        String substr = message.substring(firstTile);
                        String[] pos = substr.split(" ");
                        List<Position> tilesSelected = new ArrayList<>();
                        for (String s : pos) {
                            tilesSelected.add(new Position(Integer.parseInt(s.split(",")[0]) - 1, Integer.parseInt(s.split(",")[1]) - 1));
                        }
                        Action a = new Action(Action.ActionType.PICKTILES, nickname, "", "", tilesSelected, null);
                        sendAction(a);
                    } else if (message.startsWith("/column")) {
                        int index = "/column ".length();
                        String substr = message.substring(index);
                        int col = Integer.parseInt(substr.substring(0, 1)) - 1;
                        if (col < 0 || col > 5) {
                            System.out.println("Invalid column number");
                        } else {
                            Action a = new Action(Action.ActionType.SELECTCOLUMN, nickname, "", "", null, col);
                            sendAction(a);
                        }
                    } else if (message.startsWith("/printboard")) {
                        Action a = new Action(Action.ActionType.PRINTBOARD, nickname, "", null, null, null);
                        sendAction(a);
                    } else if (message.startsWith("/order")) {
                        int index = "/order".length() + 1;
                        String substr = message.substring(index);
                        List<String> tiles = List.of(substr.split(" "));
                        List<String> newOrder = new ArrayList<>();
                        for (String t : tiles) {
                            if (isColor(t)) {
                                newOrder.add(t);
                            }
                        }
                        if (newOrder.size() == tiles.size() && new HashSet<>(newOrder).containsAll(tiles)) {
                            StringBuilder builder = new StringBuilder();
                            for (int i = 0; i < newOrder.size(); i++) {
                                builder.append(newOrder.get(i));
                                if (i != newOrder.size() - 1) {
                                    builder.append(" ");
                                }
                            }
                            Action a = new Action(Action.ActionType.ORDER, nickname, "", builder.toString(), null, null);
                            sendAction(a);
                        } else if (!new HashSet<>(newOrder).containsAll(tiles)) {
                            System.out.println("You must chose the tiles you have collected");
                        } else {
                            System.out.println("Wrong command syntax. Use: /order [color1][color2][color3](opt.)");
                        }

                    } else if (message.startsWith("/help")) {
                        sendAction(new Action(Action.ActionType.HELP, nickname, null, null, null, null));
                    } else {
                        Action a = new Action(Action.ActionType.INFO, nickname, "", message, null, null);
                        sendAction(a);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        }



        private boolean isColor(String s) {
            return s.equals("W") || s.equals("B") || s.equals("L") ||
                    s.equals("P") || s.equals("G") || s.equals("Y");
        }
    }

    class RMIInputHandler implements Runnable {

        public void run() {
            String message;
            BufferedReader inReader = new BufferedReader(new InputStreamReader(System.in));
            while (!done) {
                try {
                    message = inReader.readLine();

                    if (message.equals("/quit")) {
                        rmiServer.quit(nickname);
                        inReader.close();
                        shutdown();
                    } else if (message.startsWith("/chat")) {
                        rmiServer.chatMessage(nickname,message.substring(message.indexOf("/chat") + "/chat ".length()));
                    }
                    /*
                     * /collect x1,y1 (opt)x2,y2 (opt)x3,y3
                     */
                    else if (message.startsWith("/collect")) {
                        int firstTile = "/collect ".length();
                        String substr = message.substring(firstTile);
                        String[] pos = substr.split(" ");
                        List<Position> tilesSelected = new ArrayList<>();
                        for (String s : pos) {
                            tilesSelected.add(new Position(Integer.parseInt(s.split(",")[0]) - 1, Integer.parseInt(s.split(",")[1]) - 1));
                        }
                        rmiServer.pickTiles(nickname,tilesSelected);
                    } else if (message.startsWith("/column")) {
                        int index = "/column ".length();
                        String substr = message.substring(index);
                        int col = Integer.parseInt(substr.substring(0, 1)) - 1;
                        if (col < 0 || col > 5) {
                            System.out.println("Invalid column number");
                        } else {
                            rmiServer.selectColumn(nickname,col);
                        }
                    } else if (message.startsWith("/order")) {
                        int index = "/order".length() + 1;
                        String substr = message.substring(index);
                        List<String> tiles = List.of(substr.split(" "));
                        List<String> newOrder = new ArrayList<>();
                        for (String t : tiles) {
                            if (isColor(t)) {
                                newOrder.add(t);
                            }
                        }
                        if (newOrder.size() == tiles.size() && new HashSet<>(newOrder).containsAll(tiles)) {
                            StringBuilder builder = new StringBuilder();
                            for (int i = 0; i < newOrder.size(); i++) {
                                builder.append(newOrder.get(i));
                                if (i != newOrder.size() - 1) {
                                    builder.append(" ");
                                }
                            }
                            rmiServer.order(nickname,builder.toString());
                        } else if (!new HashSet<>(newOrder).containsAll(tiles)) {
                            System.out.println("You must chose the tiles you have collected");
                        } else {
                            System.out.println("Wrong command syntax. Use: /order [color1][color2][color3](opt.)");
                        }

                    } else if (message.startsWith("/help")) {
                        rmiServer.help(nickname);
                    } else {
                        rmiServer.infoMessage(nickname,message);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        }



        private boolean isColor(String s) {
            return s.equals("W") || s.equals("B") || s.equals("L") ||
                    s.equals("P") || s.equals("G") || s.equals("Y");
        }
    }

    private Response recieveResponse(String jString) throws IOException {
        return JsonParser.getResponse(jString);
    }

    private synchronized void sendAction(Action action) throws IOException {
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
        shutdown();
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
            while (isRunning() && time < Constants.PINGTHRESHOLD/Constants.PINGFACTOR) {
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




package it.polimi.myShelfie.network.inputHandlers;

import it.polimi.myShelfie.application.Client;
import it.polimi.myShelfie.application.GUIClient;
import it.polimi.myShelfie.network.RMI.RMIServer;
import it.polimi.myShelfie.utilities.Position;
import it.polimi.myShelfie.utilities.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class RMIInputHandler extends Thread {
    private final Client client;
    private final BufferedReader inReader;
    private final List<String> inputGUI;
    private boolean isGUI;

    public RMIInputHandler(Client client) {
        this.client = client;
        inReader = new BufferedReader(new InputStreamReader(System.in));
        inputGUI = new ArrayList<>();
    }

    public void setGUI(boolean GUI) {
        isGUI = GUI;
    }

    private synchronized String getGuiAction() throws InterruptedException {
        String message;
        while(inputGUI.size()==0){
            this.wait();
        }
        message = inputGUI.get(0);
        inputGUI.remove(0);
        return message;
    }
    public synchronized void addGuiAction(String action){
        inputGUI.add(action);
        this.notifyAll();
    }

    public void run() {
        String message;
        String nickname;
        RMIServer rmiServer = client.getRmiServer();
        try {
            if(!isGUI){
                nickname = inReader.readLine();
            }else{
                nickname = getGuiAction();
            }

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        try {
            boolean connected = rmiServer.login(nickname, client);
            while (!connected) {
                if(!Utils.checkNicknameFormat(nickname)) {
                    client.nicknameDenied("Invalid nickname");
                }
                else{
                    client.nicknameDenied("Nickname already use, retry:");
                }
                if(!isGUI) {
                    nickname = inReader.readLine();
                }else{
                    nickname = getGuiAction();
                }
                connected = rmiServer.login(nickname, client);
            }
        } catch (Exception e) {
            //thread ends execution doing nothing
        }
        client.setNickname(nickname);
        while (!client.getDone()) {
            try {
                if(!isGUI){
                    message = inReader.readLine();
                }else{
                    message = getGuiAction();
                }

                if (message.equals("/quit")) {
                    client.getRmiServer().quit(client.getNickname());
                    client.remoteShutdown("");
                } else if (message.startsWith("/chat")) {
                    if(message.charAt("/chat".length())!=' '){
                        client.getRmiServer().chatMessage(client.getNickname(), message.substring( "/chat".length()));
                    }else {
                        client.getRmiServer().chatMessage(client.getNickname(), message.substring("/chat ".length()));
                    }
                    if(client.isGUI()){
                        GUIClient.getInstance().addMyChatMessage(message.substring(message.indexOf("/chat") + "/chat ".length()));
                    }
                }
                else if(message.startsWith("/pvt-")){
                    List<String> splittedMessage = Arrays.stream(message.split(" ")).toList();
                    if(splittedMessage.stream().filter(s -> !s.equals(" ")).toList().size() > 1) {
                        client.getRmiServer().privateMessage(client.getNickname(), message.substring(message.indexOf("pvt-") + "pvt-".length()));
                        if (client.isGUI()) {
                            GUIClient.getInstance().addMyChatMessage(message.substring(message.indexOf(" ")));
                        }
                    }
                }
                /*
                 * /collect x1,y1 (opt)x2,y2 (opt)x3,y3
                 */
                else if (message.startsWith("/collect")) {
                    int firstTile = "/collect ".length();
                    String substr = message.substring(firstTile);
                    String[] pos = substr.split(" ");
                    if(pos.length<1||pos.length>3){
                        System.err.println("Wrong syntax, try again");
                    }else {
                        List<Position> tilesSelected = new ArrayList<>();
                        for (String s : pos) {
                            try {
                                tilesSelected.add(new Position(Integer.parseInt(s.split(",")[0]) - 1, Integer.parseInt(s.split(",")[1]) - 1));
                            } catch (Exception e) {
                                System.err.println("Wrong syntax, try again");
                                tilesSelected.clear();
                                break;
                            }
                        }
                        if (tilesSelected.size() != 0) {
                            client.getRmiServer().pickTiles(client.getNickname(), tilesSelected);
                        }
                    }
                } else if (message.startsWith("/column")) {
                    message = message.replace(" ", "");
                    int index = "/column".length();
                    String substr;
                    int col=-1;
                    try {
                        substr = message.substring(index);
                        col = Integer.parseInt(substr.substring(0, 1)) - 1;
                    }catch(Exception e){
                        System.out.println("Invalid sintax");
                    }
                    if ((col < 0 || col > 5)&&(col!=-1)){
                        System.out.println("Invalid column number");
                    } else if(col!=-1){
                        client.getRmiServer().selectColumn(client.getNickname(), col);
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
                        client.getRmiServer().order(client.getNickname(), builder.toString());
                    } else if (!new HashSet<>(newOrder).containsAll(tiles)) {
                        System.out.println("You must chose the tiles you have collected");
                    } else {
                        System.out.println("Wrong command syntax. Use: /order [color1][color2][color3](opt.)");
                    }

                } else if (message.startsWith("/help")) {
                    client.getRmiServer().help(client.getNickname());
                }else if(message.startsWith("/menu")){
                    client.getRmiServer().requestMenu(client.getNickname());
                }else {
                    client.getRmiServer().infoMessage(client.getNickname(), message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
        System.out.println("Quitting RMIinputHandler");
    }


    private boolean isColor(String s) {
        return s.equals("W") || s.equals("B") || s.equals("L") ||
                s.equals("P") || s.equals("G") || s.equals("Y");
    }
}

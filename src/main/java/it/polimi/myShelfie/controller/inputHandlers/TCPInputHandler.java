package it.polimi.myShelfie.controller.inputHandlers;

import it.polimi.myShelfie.application.Client;
import it.polimi.myShelfie.application.GUIClient;
import it.polimi.myShelfie.utilities.Position;
import it.polimi.myShelfie.utilities.pojo.Action;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class TCPInputHandler extends Thread{
    private final Client client;
    private boolean isGUI;
    private final List<String> inputGUI;

    public TCPInputHandler(Client client){
        this.client = client;
        inputGUI = new ArrayList<>();
    }

    public void setGUI(boolean GUI) {
        isGUI = GUI;
    }

    @Override
    public void run() {
        String message;
        BufferedReader inReader = null;


        if(!isGUI){
            inReader = new BufferedReader(new InputStreamReader(System.in));
        }

        while (!client.getDone()) {
            try {
                if (!isGUI) {
                    try {
                        message = inReader.readLine();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    message = getGuiAction();
                }

                if (message.equals("/quit")) {
                    Action a = new Action(Action.ActionType.QUIT, client.getNickname(), "", "", null, null);
                    client.sendAction(a);
                    if (!isGUI){
                        inReader.close();
                    }
                    client.shutdown();
                } else if (message.startsWith("/chat")) {
                    Action a = new Action(Action.ActionType.CHAT, client.getNickname(), message.substring(message.indexOf("/chat") + "/chat ".length()), "", null, null);
                    if(client.isGUI()) {
                        GUIClient.getInstance().addMyChatMessage(message.substring(message.indexOf("/chat") + "/chat ".length()));
                    }
                    client.sendAction(a);
                }
                else if(message.startsWith("/pvt-")){
                    List<String> splitMessage = Arrays.stream(message.split(" ")).toList();
                    if(splitMessage.stream().filter(s -> !s.equals(" ")).toList().size() > 1) {
                        Action a = new Action(Action.ActionType.PRIVATEMESSAGE, client.getNickname(), message.substring("/pvt- ".length() - 1), "", null, null);
                        if(client.isGUI()){
                            GUIClient.getInstance().addMyChatMessage(message.substring(message.indexOf(" ")));
                        }
                        client.sendAction(a);
                    }
                }
                /*
                 * /collect x1,y1 (opt)x2,y2 (opt)x3,y3
                 */
                else if (message.startsWith("/collect")) {
                    int firstTile = "/collect ".length();
                    String substr = message.substring(firstTile);
                    String[] pos = substr.split(" ");
                    if (pos.length < 1 || pos.length > 3) {
                        System.err.println("Wrong syntax, try again");
                    } else {
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
                            Action a = new Action(Action.ActionType.PICKTILES, client.getNickname(), "", "", tilesSelected, null);
                            client.sendAction(a);
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
                        System.out.println("Invalid syntax");
                    }
                    if ((col < 0 || col > 5)&&(col!=-1)){
                        System.out.println("Invalid column number");
                    } else if(col!=-1){
                        Action a = new Action(Action.ActionType.SELECTCOLUMN, client.getNickname(), "", "", null, col);
                        client.sendAction(a);
                    }
                } else if (message.startsWith("/order")) {
                    int index = "/order ".length();
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
                        Action a = new Action(Action.ActionType.ORDER, client.getNickname(), "", builder.toString(), null, null);
                        client.sendAction(a);
                    } else if (!new HashSet<>(newOrder).containsAll(tiles)) {
                        System.out.println("You must chose the tiles you have collected");
                    } else {
                        System.out.println("Wrong command syntax. Use: /order [color1][color2][color3](opt.)");
                    }

                } else if (message.startsWith("/help")) {
                    client.sendAction(new Action(Action.ActionType.HELP, client.getNickname(), null, null, null, null));
                }else if(message.startsWith("/menu")){
                    client.sendAction(new Action(Action.ActionType.REQUEST_MENU, client.getNickname(), null, null, null, null));
                } else {
                    Action a = new Action(Action.ActionType.INFO, client.getNickname(), "", message, null, null);
                    client.sendAction(a);
                }
            }catch (IOException e){
                e.printStackTrace();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }



    private boolean isColor(String s) {
        return s.equals("W") || s.equals("B") || s.equals("L") ||
                s.equals("P") || s.equals("G") || s.equals("Y");
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
}

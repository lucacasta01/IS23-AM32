package it.polimi.myShelfie.application;

import it.polimi.myShelfie.utilities.Position;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class RMIInputHandler extends Thread {
    private Client client;
    private final BufferedReader inReader;

    public RMIInputHandler(Client client) {
        this.client = client;
        inReader = new BufferedReader(new InputStreamReader(System.in));
    }
     public void closeBufferedReader(){
        try{
            inReader.close();
            System.out.println("BufferedReader closed");
        }catch(Exception e){
            e.printStackTrace();
        }
     }

    public void run() {
        String message;
        while (!client.getDone()) {
            try {
                message = inReader.readLine();
                if (message.equals("/quit")) {
                    client.getRmiServer().quit(client.getNickname());
                    client.remoteShutdown("");
                } else if (message.startsWith("/chat")) {
                    client.getRmiServer().chatMessage(client.getNickname(), message.substring(message.indexOf("/chat") + "/chat ".length()));
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
                    client.getRmiServer().pickTiles(client.getNickname(), tilesSelected);
                } else if (message.startsWith("/column")) {
                    int index = "/column ".length();
                    String substr = message.substring(index);
                    int col = Integer.parseInt(substr.substring(0, 1)) - 1;
                    if (col < 0 || col > 5) {
                        System.out.println("Invalid column number");
                    } else {
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
                } else {
                    client.getRmiServer().infoMessage(client.getNickname(), message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        System.out.println("Quitting RMIinputHandler");
    }


    private boolean isColor(String s) {
        return s.equals("W") || s.equals("B") || s.equals("L") ||
                s.equals("P") || s.equals("G") || s.equals("Y");
    }
}

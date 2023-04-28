package it.polimi.myShelfie.server;

import it.polimi.myShelfie.model.Game;
import it.polimi.myShelfie.model.Player;
import it.polimi.myShelfie.model.Position;
import it.polimi.myShelfie.model.Tile;
import it.polimi.myShelfie.utilities.JsonParser;
import it.polimi.myShelfie.utilities.Utils;
import it.polimi.myShelfie.utilities.beans.Action;
import it.polimi.myShelfie.utilities.beans.GameParameters;

import java.io.IOException;
import java.util.*;

public class Lobby implements Runnable{
    private final List<ClientHandler> lobbyPlayers;
    private String lobbyUID;
    private Integer playersNumber;
    private GameMode gameMode;
    private boolean isOpen;
    private boolean ended = false;
    private List<Tile> collectedTiles;

    private Game game;
    public final List<Action> actions = new ArrayList<>();
    public final Object locker = new Object();

    /**
     * Create a lobby for a new game
     *
     * @param lobbyHost the lobby creator
     * @param lobbyUID
     * @param playersNumber
     */
    public Lobby(ClientHandler lobbyHost, String lobbyUID, int playersNumber) {
        this.lobbyPlayers = new ArrayList<>();
        lobbyPlayers.add(lobbyHost);
        this.lobbyUID = lobbyUID;
        this.playersNumber = playersNumber;
        this.gameMode = GameMode.NEWGAME;
        this.isOpen = true;
    }

    /**
     * create a lobby to load a saved game
     * @param lobbyHost
     * @param lobbyUID
     */
    public Lobby(ClientHandler lobbyHost, String lobbyUID) {
        this.lobbyPlayers = new ArrayList<>();
        lobbyPlayers.add(lobbyHost);
        lobbyHost.setPlaying(true);
        this.lobbyUID = lobbyUID;
        this.gameMode = GameMode.SAVEDGAME;
        this.isOpen = false;
    }

    @Override
    public void run(){
        switch (gameMode){
            case NEWGAME -> {
                lobbyPlayers.get(0).sendInfoMessage("Enter number of players: ");
                while(actions.size()==0){
                    try {
                        synchronized (actions){
                            actions.wait();
                        }
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    Action action = actions.get(0);
                    if(action.getActionType() == Action.ActionType.INFO) {
                        if(!action.getInfo().equals("2")&&!action.getInfo().equals("3")&&!action.getInfo().equals("4")){
                            lobbyPlayers.get(0).sendDeny("Invalid players number, please retry");
                            actions.remove(0);
                        }

                    }else if(action.getActionType()== Action.ActionType.CHAT){
                        lobbyPlayers.get(0).sendDeny("The chat is not active now");
                        actions.remove(0);
                    }else if(action.getActionType()== Action.ActionType.PICKTILES){
                        lobbyPlayers.get(0).sendDeny("Cannot pick tiles here, game not started");
                        actions.remove(0);
                    }else if(action.getActionType()== Action.ActionType.SELECTCOLUMN){
                        lobbyPlayers.get(0).sendDeny("Cannot select column here, game not started");
                        actions.remove(0);
                    }

                }
                playersNumber = Integer.parseInt(actions.get(0).getInfo());
                actions.remove(0);
                game = new Game(lobbyUID,playersNumber);
                try {
                    broadcastMessage("Waiting for players..."+" "+"("+getLobbySize()+"/"+getPlayersNumber()+")");
                    waitForPlayers();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                broadcastMessage("Starting new game");
                this.isOpen = false;
                game.addPlayer(generatePlayers());
                game.saveGame();
                broadcastMessage("Game board:\n"+game.getGameBoard().toString());

                while(!ended) {
                    while (actions.size() == 0) {
                        synchronized (actions) {
                            try {
                                actions.wait();
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                    synchronized (actions){
                        Iterator<Action> iter = actions.iterator();
                        while(iter.hasNext()){
                            Action a = iter.next();
                            String nickname = a.getNickname();
                            ClientHandler ch = clientHandlerOf(a.getNickname());
                            if(a.getActionType()== Action.ActionType.INFO){
                                System.out.println("Info from:"+nickname+" "+a.getInfo());
                                iter.remove();
                            } else if (a.getActionType()== Action.ActionType.CHAT) {
                                sendChat(nickname, a.getChatMessage());
                                iter.remove();
                            }else if(a.getActionType()== Action.ActionType.PICKTILES){
                                if(game.getPlayers().get(game.getCurrentPlayer()).getUsername().equals(nickname)){
                                   List<Position> tiles = a.getChosenTiles();
                                   collectedTiles = game.collectTiles(tiles);
                                   if(collectedTiles == null){
                                       if(ch!=null){
                                           ch.sendDeny("Cannot pick those tiles");
                                       }
                                   }else{
                                       StringBuilder builder = new StringBuilder();
                                       builder.append("picked tiles: ");
                                       for(Tile t: collectedTiles){
                                           builder.append(t.toString()+" ");
                                       }
                                       ch.sendInfoMessage(builder.toString());
                                       ch.sendInfoMessage("Insert column");
                                   }


                                }else{
                                    if(ch!=null){
                                        ch.sendDeny("Is not your turn...");
                                    }
                                }
                                iter.remove();
                            }else if(a.getActionType()== Action.ActionType.SELECTCOLUMN){
                                if(game.getPlayers().get(game.getCurrentPlayer()).getUsername().equals(nickname)){
                                    if(collectedTiles==null){
                                        ch.sendDeny("Not selected tiles yet...");
                                    }else{
                                        if(game.insertTiles(collectedTiles, a.getChosenColumn())){
                                            ch.sendAccept("Tiles inserted correctly");
                                            game.handleTurn();
                                        }else{
                                            ch.sendDeny("Cannot insert tiles in this column...");
                                        }
                                        ch.sendInfoMessage(game.getPlayers().get(game.getCurrentPlayer()).getMyShelf().toString());
                                    }
                                }else {
                                    if (ch != null) {
                                        ch.sendDeny("Is not your turn...");
                                    }
                                }
                                iter.remove();
                            }else if(a.getActionType()== Action.ActionType.PRINTBOARD){
                                if(ch!=null){
                                    ch.sendInfoMessage(game.getGameBoard().toString());
                                }
                                iter.remove();
                            }else if(a.getActionType()==Action.ActionType.ORDER){
                                if(game.getPlayers().get(game.getCurrentPlayer()).getUsername().equals(nickname)) {
                                    String order = a.getInfo();
                                    String[] Tiles = order.split(" ");
                                    List<Tile> toInsert = new ArrayList<>();
                                    if (this.collectedTiles != null) {
                                        if (this.collectedTiles.size()!=0) {
                                            for(String s:Tiles){
                                                toInsert.add(this.collectedTiles.get(Integer.parseInt(s)));
                                            }
                                            this.collectedTiles.clear();
                                            this.collectedTiles.addAll(toInsert);
                                            if(ch!=null){
                                                StringBuilder string = new StringBuilder();
                                                for(Tile t: this.collectedTiles){
                                                    string.append(t.getColor().toString()).append(" ");
                                                }
                                                ch.sendAccept("Order changed successfully"+string.toString());

                                            }
                                    }else{
                                            if(ch!=null) {
                                                ch.sendDeny("You haven't picked tiles yet...");
                                            }
                                        }
                                    } else {
                                        if(ch!=null) {
                                            ch.sendDeny("You haven't picked tiles yet...");
                                        }
                                    }
                                }else{
                                    if(ch!=null){
                                        ch.sendDeny("It's not your turn...");
                                    }
                                }
                                iter.remove();
                            }else if(a.getActionType()== Action.ActionType.LOBBYKILL){
                                game.saveGame();
                                for(ClientHandler client:lobbyPlayers){
                                    client.sendInfoMessage("Lobby is killing");
                                }
                                ended=true;
                            }
                        }
                    }


                }
                broadcastUpdate();
            }
            case SAVEDGAME -> {
                try{
                    game = new Game(lobbyUID);
                }catch(Exception e){
                    broadcastMessage("No configuration file found...");
                    Server server = Server.getInstance();
                    server.killLobby(lobbyUID);
                }

                playersNumber = game.getPlayersNumber();
                //read json from controller


            }
        }
    }

    private List<Player> generatePlayers(){
        List<Player> toReturn = new ArrayList<>();
        for(ClientHandler p : lobbyPlayers){
            toReturn.add(new Player(p.getNickname(),p.getClientSocket().getLocalAddress().getHostAddress()));
        }
        return toReturn;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public List<ClientHandler> getLobbyPlayers() {
        return lobbyPlayers;
    }

    public String getLobbyUID() {
        return lobbyUID;
    }

    public int getPlayersNumber() {
        return playersNumber;
    }

    public int getLobbySize(){
        return lobbyPlayers.size();
    }


    private void waitForPlayers() throws InterruptedException {
        synchronized (lobbyPlayers) {
            while (getLobbySize() < getPlayersNumber()) {
                lobbyPlayers.wait();

            }
        }
    }

    public void clientError(ClientHandler ch){
        lobbyPlayers.remove(ch);
        ch.setPlaying(false);
        broadcastMessage(ch.getNickname() + " connection lost");
        broadcastMessage("Game is closing...");
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void acceptPlayer(ClientHandler player) throws RuntimeException{
        player.setPlaying(true);
        synchronized (lobbyPlayers) {
            if (lobbyPlayers.size() + 1 > this.playersNumber) {
                throw new RuntimeException("player number exceeded");
            }
        }
        synchronized (lobbyPlayers){
            lobbyPlayers.add(player);
            broadcastMessage(player.getNickname()+" joined the lobby "+"("+getLobbySize()+"/"+getPlayersNumber()+")");
            lobbyPlayers.notifyAll();
        }
    }

    public void broadcastMessage(String message){
        for(ClientHandler t : lobbyPlayers){
            t.sendInfoMessage(message);
        }
    }
    public void sendChat(String sender, String message){
        for(ClientHandler ch : lobbyPlayers){
            if(!ch.getNickname().equals(sender)){
                ch.sendChatMessage(message, sender);
            }
        }
    }

    public void broadcastUpdate(){
        for(ClientHandler t : lobbyPlayers){
            t.sendUpdateRequest();
        }
    }

    public void shutdown(){
        for(ClientHandler t : lobbyPlayers){
            //REPLACE WITH: GOTO MENU
            t.shutdown();
        }

    }
    public synchronized void recieveAction(Action a){
        actions.add(a);
    }

    private ClientHandler clientHandlerOf(String nickname){
        for(ClientHandler ch: lobbyPlayers){
            if(ch.getNickname().equals(nickname)){
                return ch;
            }
        }
        return null;
    }


    public enum GameMode{
        NEWGAME,
        SAVEDGAME
    }
}


/*
*           while (in != null && (message = in.readLine()) != null) {
                if (message.startsWith("/quit")) {
                    server.removeClient(this);
                    server.getUserGame().remove(nickname);
                    shutdown();
                    System.out.println(nickname+" disconnected");
                    server.broadcastMessage(nickname+" disconnected");
                    break;
                } else {
                    Server.getInstance().broadcastMessage(nickname + ": " + message);
                }
            }*/

/*

 */
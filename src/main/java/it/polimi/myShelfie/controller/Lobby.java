package it.polimi.myShelfie.controller;

import it.polimi.myShelfie.application.Server;
import it.polimi.myShelfie.model.Game;
import it.polimi.myShelfie.model.Player;
import it.polimi.myShelfie.model.Position;
import it.polimi.myShelfie.model.Tile;
import it.polimi.myShelfie.model.cards.PersonalGoalCard;
import it.polimi.myShelfie.utilities.ANSI;
import it.polimi.myShelfie.utilities.beans.Action;
import it.polimi.myShelfie.utilities.beans.View;

import java.util.*;

public class Lobby implements Runnable{
    private final List<ClientHandler> lobbyPlayers;
    private String lobbyUID;
    private Integer playersNumber;
    private GameMode gameMode;
    private boolean isOpen;
    private boolean ended = false;
    private List<Tile> collectedTiles;
    private Stack<String> colors;

    private Game game;
    public final List<Action> actions = new ArrayList<>();

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
        lobbyHost.setColor(ANSI.BLUE);
        this.lobbyUID = lobbyUID;
        this.playersNumber = playersNumber;
        this.gameMode = GameMode.NEWGAME;
        this.isOpen = true;
        this.colors = new Stack<>();
        colors.push(ANSI.PURPLE);
        colors.push(ANSI.GREEN);
        colors.push(ANSI.YELLOW);

    }

    /**
     * create a lobby to load a saved game
     * @param lobbyHost
     * @param lobbyUID
     */
    public Lobby(ClientHandler lobbyHost, String lobbyUID) {
        this.lobbyPlayers = new ArrayList<>();
        lobbyPlayers.add(lobbyHost);
        lobbyHost.setColor(ANSI.BLUE);
        lobbyHost.setPlaying(true);
        this.lobbyUID = lobbyUID;
        this.gameMode = GameMode.SAVEDGAME;
        this.isOpen = false;
        this.colors = new Stack<>();
        colors.push(ANSI.PURPLE);
        colors.push(ANSI.GREEN);
        colors.push(ANSI.YELLOW);
    }

    @Override
    public void run(){
        boolean close = false;
        if(this.gameMode == GameMode.NEWGAME){
            lobbyPlayers.get(0).sendInfoMessage("Enter number of players: ");
            while(actions.size()==0) {
                try {
                    synchronized (actions) {
                        actions.wait();
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                Action action = actions.get(0);
                if (action.getActionType() == Action.ActionType.INFO) {
                    if ((!action.getInfo().equals("2") && !action.getInfo().equals("3") && !action.getInfo().equals("4"))) {
                        lobbyPlayers.get(0).sendDeny("Invalid players number, please retry");
                        actions.remove(0);
                    }

                } else if (action.getActionType() == Action.ActionType.CHAT) {
                    lobbyPlayers.get(0).sendDeny("The chat is not active now");
                    actions.remove(0);
                } else if (action.getActionType() == Action.ActionType.PICKTILES) {
                    lobbyPlayers.get(0).sendDeny("Cannot pick tiles here, game not started");
                    actions.remove(0);
                } else if (action.getActionType() == Action.ActionType.SELECTCOLUMN) {
                    lobbyPlayers.get(0).sendDeny("Cannot select column here, game not started");
                    actions.remove(0);
                } else if (action.getActionType() == Action.ActionType.QUIT) {
                    Server.getInstance().getLobbyList().remove(this);
                    Server.getInstance().removeClient(clientHandlerOf(lobbyPlayers.get(0).getNickname()));
                    Server.getInstance().getUserGame().remove(lobbyPlayers.get(0).getNickname());
                    Server.getInstance().saveUserGame();
                    System.out.println("Lobby " + this.lobbyUID + " killed");
                    lobbyPlayers.get(0).sendShutdown();
                    close = true;
                }
            }
            if(!close) {
                playersNumber = Integer.parseInt(actions.get(0).getInfo());
                actions.remove(0);
                game = new Game(lobbyUID, playersNumber);
                try {
                    broadcastMessage("Waiting for players..." + " " + "(" + getLobbySize() + "/" + getPlayersNumber() + ")");
                    waitForPlayers();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                game.addPlayer(generatePlayers());
            }
        }
        else if(this.gameMode == GameMode.SAVEDGAME && !close){
            try{
                game = new Game(lobbyUID);
            }catch(Exception e){
                broadcastMessage("No configuration file found...");
                Server.getInstance().getLobbyList().remove(this);
                Server.getInstance().removeClient(clientHandlerOf(lobbyPlayers.get(0).getNickname()));
                Server.getInstance().getUserGame().remove(lobbyPlayers.get(0).getNickname());
                Server.getInstance().saveUserGame();
                System.out.println("Lobby " + this.lobbyUID + " killed");
                lobbyPlayers.get(0).sendShutdown();
                close = true;
            }

            playersNumber = game.getOldGamePlayers().size();
            try {
                broadcastMessage("Waiting for players..." + " " + "(" + getLobbySize() + "/" + getPlayersNumber() + ")");
                waitForPlayers();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            game.setPlayers(game.getOldGamePlayers());
        }

        if(!close){
            broadcastMessage("Starting new game");
            for (ClientHandler ch : lobbyPlayers) {
                sendCommands(ch);
            }
            this.isOpen = false;
            game.saveGame();
            broadcastUpdate();
            while (!ended) {
                while (actions.size() == 0) {
                    synchronized (actions) {
                        try {
                            actions.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                synchronized (actions) {
                    Iterator<Action> iter = actions.iterator();
                    while (iter.hasNext()) {
                        Action a = iter.next();
                        String nickname = a.getNickname();
                        ClientHandler ch = clientHandlerOf(a.getNickname());
                        if (a.getActionType() == Action.ActionType.INFO) {
                            System.out.println("Info from:" + nickname + " " + a.getInfo());
                            iter.remove();
                        } else if (a.getActionType() == Action.ActionType.CHAT) {
                            sendChat(ANSI.BOLD + ch.getColor() + nickname + ANSI.RESET_COLOR + ANSI.RESET_STYLE, ANSI.ITALIQUE + a.getChatMessage() + ANSI.RESET_STYLE);
                            iter.remove();
                        } else if (a.getActionType() == Action.ActionType.PICKTILES) {
                            if (game.getPlayers().get(game.getCurrentPlayer()).getUsername().equals(nickname)) {
                                List<Position> tiles = a.getChosenTiles();
                                collectedTiles = game.collectTiles(tiles);
                                if (collectedTiles == null) {
                                    if (ch != null) {
                                        ch.sendDeny("Cannot pick those tiles");
                                    }
                                } else {
                                    StringBuilder builder = new StringBuilder();
                                    builder.append("picked tiles: ");
                                    for (Tile t : collectedTiles) {
                                        builder.append(t.toString() + " ");
                                    }
                                    ch.sendInfoMessage(builder.toString());
                                    ch.sendInfoMessage("Insert column or change order");
                                }


                            } else {
                                if (ch != null) {
                                    ch.sendDeny("Is not your turn...");
                                }
                            }
                            iter.remove();
                        } else if (a.getActionType() == Action.ActionType.SELECTCOLUMN) {
                            if (game.getPlayers().get(game.getCurrentPlayer()).getUsername().equals(nickname)) {
                                if (collectedTiles == null) {
                                    ch.sendDeny("Not selected tiles yet...");
                                } else {
                                    if (game.insertTiles(collectedTiles, a.getChosenColumn())) {
                                        ch.sendAccept("Tiles inserted correctly");
                                        ch.sendInfoMessage(game.getPlayers().get(game.getCurrentPlayer()).getMyShelf().toString());
                                        broadcastUpdate();
                                        handleTurn(ch);
                                    } else {
                                        ch.sendDeny("Cannot insert tiles in this column...");
                                    }
                                }
                            } else {
                                if (ch != null) {
                                    ch.sendDeny("Is not your turn...");
                                }
                            }
                            iter.remove();
                        } else if (a.getActionType() == Action.ActionType.PRINTBOARD) {
                            if (ch != null) {
                                ch.sendInfoMessage(game.getGameBoard().toString());
                            }
                            iter.remove();
                        } else if (a.getActionType() == Action.ActionType.ORDER) {
                            if (game.getPlayers().get(game.getCurrentPlayer()).getUsername().equals(nickname)) {
                                String order = a.getInfo();
                                String[] colors = order.split(" ");
                                Tile[] tiles = new Tile[colors.length];
                                if (this.collectedTiles != null) {
                                    if (this.collectedTiles.size() != 0) {
                                        for (String s : colors) {
                                            for (Tile t : collectedTiles) {
                                                if (t.toString().equals(s)) {
                                                    tiles[List.of(colors).indexOf(s)] = new Tile(t.getImagePath(), t.getColor());
                                                }
                                            }
                                        }

                                        this.collectedTiles.clear();
                                        this.collectedTiles.addAll(List.of(tiles));
                                        if (ch != null) {
                                            StringBuilder string = new StringBuilder();
                                            for (Tile t : this.collectedTiles) {
                                                string.append(t.getColor().toString()).append(" ");
                                            }
                                            ch.sendAccept("Order changed successfully " + string.toString());

                                        }
                                    } else {
                                        if (ch != null) {
                                            ch.sendDeny("You haven't picked tiles yet...");
                                        }
                                    }
                                } else {
                                    if (ch != null) {
                                        ch.sendDeny("You haven't picked tiles yet...");
                                    }
                                }
                            } else {
                                if (ch != null) {
                                    ch.sendDeny("It's not your turn...");
                                }
                            }
                            iter.remove();
                        } else if (a.getActionType() == Action.ActionType.LOBBYKILL) {
                            game.saveGame();
                            for (ClientHandler client : lobbyPlayers) {
                                client.sendInfoMessage("Lobby is being killed");
                                client.setPlaying(false);
                                client.sendMenu();
                            }
                            iter.remove();
                            ended = true;
                        }
                        else if (a.getActionType() == Action.ActionType.HELP) {
                            sendCommands(ch);
                            iter.remove();
                        }
                    }
                }
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
    private void sendCommands(ClientHandler ch){
        ch.sendHelpMessage();
    }

    private void handleTurn(ClientHandler current){
        Player p = this.game.getPlayers().get(this.game.getCurrentPlayer());
        PersonalGoalCard card = p.getMyGoalCard();
        //todo personal card checkpattern
        //check shared goal
        //check is full shelf
        //check refill board
        this.game.handleTurn();
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
        player.setColor(colors.pop());
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
            if(!(ANSI.BOLD+ch.getColor()+ch.getNickname()+ANSI.RESET_COLOR+ANSI.RESET_STYLE).equals(sender)){
                ch.sendChatMessage(message, sender);
            }
        }
    }

    public void broadcastUpdate(){
        View view = new View();
        List<String> toSend = new ArrayList<>();
        view.setBoard(this.game.getGameBoard().toString());

        for(Player p: game.getPlayers()){
            toSend.add(clientHandlerOf(p.getUsername()).getColor()+ p.getUsername()+ANSI.RESET_COLOR+"\n"+p.getMyShelf().toString());
        }

        view.setShelves(toSend);

        for(ClientHandler ch:lobbyPlayers){
            ch.sendView(view);
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
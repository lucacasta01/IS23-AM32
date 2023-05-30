package it.polimi.myShelfie.controller;

import it.polimi.myShelfie.application.Server;
import it.polimi.myShelfie.model.Game;
import it.polimi.myShelfie.model.Player;
import it.polimi.myShelfie.utilities.Settings;
import it.polimi.myShelfie.utilities.Position;
import it.polimi.myShelfie.model.Tile;
import it.polimi.myShelfie.model.cards.SharedGoalCard;
import it.polimi.myShelfie.utilities.ANSI;
import it.polimi.myShelfie.utilities.beans.Action;
import it.polimi.myShelfie.utilities.beans.ChatMessage;
import it.polimi.myShelfie.utilities.beans.View;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

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
    private Integer index = null;
    private boolean close;
    private final AtomicBoolean endWaitingPlayers = new AtomicBoolean(false);
    private boolean firstToEnd = true;
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
        this.colors = new Stack<>();
        this.isOpen = false;
        colors.push(ANSI.PURPLE);
        colors.push(ANSI.GREEN);
        colors.push(ANSI.YELLOW);
    }

    @Override
    public void run(){
        close = false;
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
                    if(lobbyPlayers.get(0).isRMI()) {
                        lobbyPlayers.get(0).sendShutdown();
                    }
                    close = true;
                }else if(action.getActionType()== Action.ActionType.REQUEST_MENU){
                    close=true;
                    lobbyPlayers.get(0).sendMenu();
                }else{
                    actions.remove(0);
                }
            }
            if(!close) {
                try {
                    playersNumber = Integer.parseInt(actions.get(0).getInfo());
                }catch (Exception e){
                    System.out.println("caught this exception");
                    e.printStackTrace();
                }
                lobbyPlayers.get(0).sendAccept("Number of players accepted");
                actions.remove(0);
                game = new Game(lobbyUID, playersNumber);
                try {
                    this.isOpen = true;
                    broadcastMessage("Waiting for players..." + " " + "(" + lobbyPlayers.size() + "/" + playersNumber + ")");
                    lobbyPlayers.get(0).notifyLobbyCreated(playersNumber);
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
                playersNumber = game.getOldGamePlayers().size();
                for(ClientHandler ch:lobbyPlayers){
                    ch.acceptLoadGame();
                }
                try {
                    broadcastMessage("Waiting for players..." + " " + "(" + lobbyPlayers.size() + "/" + playersNumber + ")");
                    lobbyPlayers.get(0).notifyLobbyCreated(playersNumber);
                    waitForPlayers();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                reorderLobbyList();
                game.setPlayers(game.getOldGamePlayers());
            }catch(Exception e){
                broadcastMessage("No configuration file found...");
                for(ClientHandler ch : lobbyPlayers){
                    ch.denyLoadGame();
                }
                Server.getInstance().getLobbyList().remove(this);
                Server.getInstance().removeClient(lobbyPlayers.get(0));
                String oldUID = this.lobbyUID;
                Server.getInstance().getUserGame().entrySet().removeIf(entry -> entry.getValue().equals(oldUID));
                Server.getInstance().saveUserGame();
                System.out.println("Lobby " + this.lobbyUID + " killed");
                close = true;
                this.lobbyPlayers.get(0).sendMenu();
            }

        }

        if(!close){
            broadcastMessage("Starting new game");
            for (ClientHandler ch : lobbyPlayers) {
                sendCommands(ch);
            }
            this.isOpen = false;
            game.saveGame();
            notifyGameStarted();
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
                            sendChat(new ChatMessage(a.getNickname(),a.getChatMessage(),ch.getColor()));
                            iter.remove();
                        }
                        else if (a.getActionType() == Action.ActionType.PRIVATEMESSAGE){
                            sendPrivateMessage(nickname,a.getChatMessage());
                            iter.remove();
                        }
                        else if (a.getActionType() == Action.ActionType.PICKTILES) {
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
                                    ch.notifyCollectAccepted();
                                }


                            } else {
                                if (ch != null) {
                                    ch.sendDeny("Is not your turn...");
                                }
                            }
                            iter.remove();
                        } else if (a.getActionType() == Action.ActionType.SELECTCOLUMN) {
                            if (game.getPlayers().get(game.getCurrentPlayer()).getUsername().equals(nickname)) {
                                if (collectedTiles.size()==0) {
                                    ch.sendDeny("Not selected tiles yet...");
                                } else {
                                    if (game.insertTiles(collectedTiles, a.getChosenColumn())) {
                                        ch.sendAccept("Tiles inserted correctly");
                                        collectedTiles.clear();
                                        endTurnChecks(ch);
                                        if(game.isLastTurn()){
                                            index = lobbyPlayers.indexOf(ch);
                                                if(index==lobbyPlayers.size()-1){
                                                    broadcastMessage("** GAME ENDED! **");
                                                    ended = true;
                                                    game.setFinished(true);
                                                }
                                        }
                                        boolean res = this.game.getGameBoard().needToRefill();
                                        if(res){
                                            this.game.getGameBoard().initBoard(this.playersNumber);
                                        }
                                        handleTurn(ch);
                                        broadcastUpdate();
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
                        else if(a.getActionType() == Action.ActionType.REQUEST_MENU){
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
            //END OF GAME CHECKS
            if(game.isFinished()){
                endGameChecks();
                broadcastMessage(game.getRank(false));
                broadcastMessage("Closing...");
                notifyGameEnded(game.getRank(true));
                for(ClientHandler ch : lobbyPlayers){
                    Server.getInstance().getUserGame().remove(ch.getNickname());
                }
                Server.getInstance().saveUserGame();

                //delete saved game
                java.net.URL savingFileURL = getClass().getResource("/config/savedgames/"+this.lobbyUID+".json");
                if(savingFileURL != null){
                    try {
                        Files.delete(Paths.get(savingFileURL.toURI()));
                        System.out.println("Ended game file deleted successfully, UID: "+ this.lobbyUID);
                    }
                    catch (Exception e){
                        throw new RuntimeException("Error while deleting ended game file");
                    }
                }

                Server.getInstance().getLobbyList().remove(this);
            }
        }

    }

    private void notifyGameEnded(String rank) {
        for(ClientHandler ch : lobbyPlayers){
            ch.notifyGameEnded(rank);
        }
    }

    private void reorderLobbyList(){
        ClientHandler[] oldGameOrder = new ClientHandler[lobbyPlayers.size()];
        for(int i = 0; i<lobbyPlayers.size(); i++){
            for(int j = 0; j<lobbyPlayers.size(); j++){
                if(this.lobbyPlayers.get(i).getNickname().equals(this.game.getOldGamePlayers().get(j).getUsername())){
                    oldGameOrder[j]=this.lobbyPlayers.get(i);
                }
            }
        }
        synchronized (lobbyPlayers) {
            lobbyPlayers.clear();
            lobbyPlayers.addAll(Arrays.stream(oldGameOrder).toList());
        }
    }

    private void notifyGameStarted() {
        for(ClientHandler ch : lobbyPlayers){
            ch.notifyGameStarted();
        }
    }

    private List<Player> generatePlayers(){
        List<Player> toReturn = new ArrayList<>();
        for(ClientHandler p : lobbyPlayers){
            Player player = new Player(p.getNickname());
            player.setGoalCard(game.drawPersonalGoal());
            toReturn.add(player);
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



    private void sendCommands(ClientHandler ch){
        ch.sendHelpMessage();
    }

    private void handleTurn(ClientHandler current){
        Player p = this.game.getPlayers().get(this.game.getCurrentPlayer());
        this.game.handleTurn();
        this.game.saveGame();
    }



    private void waitForPlayers() throws InterruptedException {
        synchronized (lobbyPlayers) {
            int oldSize = lobbyPlayers.size();
            while (lobbyPlayers.size() < playersNumber&&!endWaitingPlayers.get()) {
                if(lobbyPlayers.size()>=oldSize){
                    oldSize++;
                    lobbyPlayers.wait();
                }
                else{
                    for(ClientHandler ch : lobbyPlayers){
                        ch.sendInfoMessage("Lobby is being killed...");
                        ch.sendMenu();
                    }
                    close = true;
                    break;
                }
            }
            if(endWaitingPlayers.get()){
                for(ClientHandler ch:lobbyPlayers){
                    ch.sendMenu();
                    ch.setPlaying(false);
                }
                close=true;
            }
        }
    }


    public void notifyExit(){
        synchronized (lobbyPlayers){
            lobbyPlayers.notifyAll();
        }
    }

    public void setEndWaitingPlayers(){
        synchronized (lobbyPlayers){
            endWaitingPlayers.set(true);
            lobbyPlayers.notifyAll();
        }
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
            broadcastMessage(player.getNickname()+" joined the lobby "+"("+lobbyPlayers.size()+"/"+playersNumber+")");
            notifyNewJoin(player); //notify other players that someone else just joined the lobby
            player.notifyLobbyJoined("("+lobbyPlayers.size()+"/"+playersNumber+")");
            lobbyPlayers.notifyAll();
        }
    }

    private void notifyNewJoin(ClientHandler player){
        for(ClientHandler t : lobbyPlayers){
            if(!t.equals(player)) {
                t.notifyNewJoin("(" + lobbyPlayers.size() + "/" + playersNumber + ")");
            }
        }
    }

    public void broadcastMessage(String message){
        for(ClientHandler t : lobbyPlayers){
            t.sendInfoMessage(message);
        }
    }
    private void sendPrivateMessage(String sender, String message){
        String receiver = message.substring(0, message.indexOf(" "));
        String[] stringArray = message.split(" ");
        List<String> stringLists = new ArrayList<>(Arrays.stream(stringArray).toList());
        stringLists.remove(0);
        StringBuilder stringBuilder = new StringBuilder();
        for(String s : stringLists){
            stringBuilder.append(s+ " ");
        }
        for(ClientHandler t : lobbyPlayers){
            if(t.getNickname().equals(receiver)){
                t.sendChatMessage(new ChatMessage(sender,stringBuilder.toString(),clientHandlerOf(sender).getColor()));
            }
        }
    }

    private void sendChat(ChatMessage chatMessage){
        for(ClientHandler ch : lobbyPlayers){
            if(!ch.getNickname().equals(chatMessage.getSender())) {
                ch.sendChatMessage(chatMessage);
            }
        }
    }

    private void broadcastUpdate(){
        View view = new View();
        List<String> players = new ArrayList<>();
        List<String> GUIBoard = new ArrayList<>();
        List<List<String>> othersGUIShelves = new ArrayList<>();
        List<String> myShelf = new ArrayList<>();
        List<Integer> GUIScores = new ArrayList<>();
        List<String> GuiSharedCard = new ArrayList<>();
        List<String> shelves = new ArrayList<>();
        ClientHandler client = lobbyPlayers.get(game.getCurrentPlayer());
        view.setANSIcolor(client.getColor());
        view.setCurrentPlayer(game.getPlayers().get(game.getCurrentPlayer()).getUsername());
        //TUI board
        view.setBoard(this.game.getGameBoard().toString());
        //GUI board
        for(int i = 0; i< Settings.BOARD_DIM; i++){
            for(int j = 0; j< Settings.BOARD_DIM; j++){
                GUIBoard.add(game.getGameBoard().getGrid()[i][j].getImagePath());
            }
        }
        view.setGUIboard(GUIBoard);

        //GUI players, shelves and points

        //shared cards
        for(SharedGoalCard s:game.getSharedDeck()){
            GuiSharedCard.add(s.getImgPath());
        }
        view.setGUIsharedCards(GuiSharedCard);

        //TUI shelves
        synchronized (game.getPlayers()) {
            for (Player p : game.getPlayers()) {
                shelves.add(clientHandlerOf(p.getUsername()).getColor() + p.getUsername() + ANSI.RESET_COLOR + ": " + p.getScore() + " points\n" + p.getMyShelf().toString());
            }
        }
        view.setShelves(shelves);

        List<String> sharedCards = new ArrayList<>();
        for(SharedGoalCard c: game.getSharedDeck()){
            sharedCards.add(c.toString());
        }
        view.setSharedCards(sharedCards);

        for(ClientHandler ch:lobbyPlayers){
            for(Player p:game.getPlayers()){
                if(!p.getUsername().equals(ch.getNickname())) {
                    List<String> pShelf = new ArrayList<>();
                    for (int i = 0; i < Settings.SHELFROW; i++) {
                        for (int j = 0; j < Settings.SHELFCOLUMN; j++) {
                            pShelf.add(p.getMyShelf().getTileMartrix()[i][j].getImagePath());
                        }
                    }
                    othersGUIShelves.add(pShelf);
                }else{
                    for (int i = 0; i < Settings.SHELFROW; i++) {
                        for (int j = 0; j < Settings.SHELFCOLUMN; j++) {
                            myShelf.add(p.getMyShelf().getTileMartrix()[i][j].getImagePath());
                        }
                    }
                }
                GUIScores.add(p.getScore());
                players.add(p.getUsername());
            }
            view.setMyShelf(myShelf);
            view.setOthersGUIShelves(othersGUIShelves);
            view.setGUIScoring(GUIScores);
            view.setPlayers(players);
            view.setPersonalCard(game.chToPlayer(ch).getMyGoalCard().toString());
            view.setGUIpersonalCard(game.chToPlayer(ch).getMyGoalCard().getImgPath());
            ch.sendView(view);
            myShelf.clear();
            othersGUIShelves.clear();
            players.clear();
            GUIScores.clear();
        }
    }
    private void endTurnChecks(ClientHandler ch){
        Player p = game.chToPlayer(ch);
        List<SharedGoalCard> SharedDeck = game.getSharedDeck();
        for (int i = 0; i<SharedDeck.size(); i++){
            SharedGoalCard c = SharedDeck.get(i);
            if(!c.isAchieved(p)){
                if(c.checkPattern(p)){
                    ch.sendInfoMessage("Shared goal "+(i+1)+" achieved");
                    for(ClientHandler client : lobbyPlayers){
                        if(!client.getNickname().equals(ch.getNickname())){
                            client.sendInfoMessage("Shared goal "+(i+1)+" achieved by "+ ch.getNickname());
                        }
                    }
                    p.updateScore(c.popPointToken());
                }
            }
        }
        game.checkLastTurn(p);
    }

    private void endGameChecks(){
        for(ClientHandler ch:lobbyPlayers){
            Player p = game.chToPlayer(ch);
            p.updateScore(p.getMyGoalCard().checkPersonalGoal(p.getMyShelf()));
            //p.updateScore(p.getMyShelf().checkFinalScore());
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
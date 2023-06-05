package it.polimi.myShelfie.controller;

import it.polimi.myShelfie.application.Server;
import it.polimi.myShelfie.model.Game;
import it.polimi.myShelfie.model.Player;
import it.polimi.myShelfie.utilities.ANSI;
import it.polimi.myShelfie.utilities.pojo.Action;
import it.polimi.myShelfie.utilities.pojo.ChatMessage;
import it.polimi.myShelfie.utilities.pojo.View;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Lobby implements Runnable{
    private final List<ClientHandler> lobbyPlayers;
    private final String lobbyUID;
    private Integer playersNumber;
    private final GameMode gameMode;
    private boolean isOpen;
    private boolean ended = false;
    private final Stack<String> colors;
    public final List<Action> actions = new ArrayList<>();
    private boolean close;
    private final AtomicBoolean endWaitingPlayers = new AtomicBoolean(false);
    private final LobbyController lobbyController;
    /**
     * Create a lobby for a brand-new game
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
        lobbyController = new LobbyController();
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
        lobbyController = new LobbyController();
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
                    lobbyController.saveGame();
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
                lobbyController.setGame(new Game(lobbyUID, playersNumber));
                try {
                    this.isOpen = true;
                    broadcastMessage("Waiting for players..." + " " + "(" + lobbyPlayers.size() + "/" + playersNumber + ")");
                    lobbyPlayers.get(0).notifyLobbyCreated(playersNumber);
                    waitForPlayers();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            lobbyController.addPlayer(generatePlayers());
            }
        }
        else if(this.gameMode == GameMode.SAVEDGAME && !close){
            try{
                lobbyController.setGame(new Game(lobbyUID));
                playersNumber = lobbyController.getOldGamePlayers().size();
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
                lobbyController.setOldGamepLayers();

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
            lobbyController.saveGame();
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
                        ClientHandler ch = clientHandlerOf(nickname);
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
                            String response = lobbyController.pickTiles(a);
                            if(response.equals("0")){
                                //not your turn
                                if (ch != null) {
                                    ch.sendDeny("Is not your turn...");
                                    singleUpdate(ch);
                                }
                            }else if(response.equals("1")){
                                //cannot pick tiles
                                if (ch != null) {
                                    ch.sendDeny("Cannot pick those tiles");
                                    singleUpdate(ch);
                                }
                            }else{
                                //tiles picked
                                ch.sendInfoMessage(response);
                                ch.sendInfoMessage("Insert column or change order");
                                ch.notifyCollectAccepted();
                            }
                            iter.remove();
                        } else if (a.getActionType() == Action.ActionType.SELECTCOLUMN) {
                            String response = lobbyController.selectColumn(a);
                            switch (response) {
                                case "0" -> {
                                    //not your turn
                                    if (ch != null) {
                                        ch.sendDeny("Is not your turn...");
                                        singleUpdate(ch);
                                    }
                                }
                                case "1" -> {
                                    //cannot insert tiles in this column
                                    ch.sendDeny("Cannot insert tiles in this column...");
                                    singleUpdate(ch);
                                }
                                case "2" -> {
                                    //tiles not selected
                                    ch.sendDeny("Not selected tiles yet...");
                                    singleUpdate(ch);
                                }
                                default -> {
                                    //tiles inserted
                                    ch.sendAccept(response);
                                    endTurnChecks(ch);
                                    if (lobbyController.checkLastTurn()) {
                                        Integer index = lobbyPlayers.indexOf(ch);
                                        if (index == lobbyPlayers.size() - 1) {
                                            broadcastMessage("** GAME ENDED! **");
                                            ended = true;
                                            lobbyController.setGameFinished();
                                        }
                                    }
                                    lobbyController.handleTurn();
                                    broadcastUpdate();
                                }
                            }
                            iter.remove();
                        }else if (a.getActionType() == Action.ActionType.ORDER) {
                            String response = lobbyController.orderTiles(a);
                            if(response.equals("0")){
                                if (ch != null) {
                                    ch.sendDeny("It's not your turn...");
                                    singleUpdate(ch);
                                }
                            }else if(response.equals("1")){
                                if (ch != null) {
                                    ch.sendDeny("You haven't picked tiles yet...");
                                    singleUpdate(ch);
                                }
                            }else{
                                ch.sendAccept(response);
                            }
                            iter.remove();
                        } else if (a.getActionType() == Action.ActionType.LOBBYKILL) {
                            lobbyController.saveGame();
                            for (ClientHandler client : lobbyPlayers) {
                                client.sendInfoMessage("Lobby is being killed");
                                client.setPlaying(false);
                                client.sendMenu();
                            }
                            iter.remove();
                            ended = true;
                        }
                        else if(a.getActionType() == Action.ActionType.REQUEST_MENU){
                            lobbyController.saveGame();
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
            if(lobbyController.isGameFinished()){
                lobbyController.endGameChecks();
                broadcastMessage(lobbyController.getRank(false));
                broadcastMessage("Closing...");
                notifyGameEnded(lobbyController.getRank(true));
                for(ClientHandler ch : lobbyPlayers){
                    Server.getInstance().getUserGame().remove(ch.getNickname());
                }
                Server.getInstance().saveUserGame();

                //delete saved game
                Path savingFilePath = Paths.get(System.getProperty("user.dir") + "/config/savedgames/" +this.lobbyUID+".json");
                if(savingFilePath.toFile().isFile()){
                    try {
                        Files.delete(savingFilePath);
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
        ClientHandler[] oldGameOrder = lobbyController.reorderLobbyPlayers(this.lobbyPlayers);
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
            player.setGoalCard(lobbyController.drawPersonalGoal());
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
            stringBuilder.append(s);
            stringBuilder.append(" ");
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

private void singleUpdate(ClientHandler chToUpdate){
    View view = lobbyController.generateView(chToUpdate, this);
    chToUpdate.sendView(view);
}
    private void broadcastUpdate(){
        for(ClientHandler ch: lobbyPlayers){
            singleUpdate(ch);
        }
    }
    private void endTurnChecks(ClientHandler ch){
        String response = lobbyController.endTurnChecks(ch.getNickname());
        if(!response.equals("1")){
            ch.sendInfoMessage(response);
            for(ClientHandler client : lobbyPlayers){
                if(!client.getNickname().equals(ch.getNickname())){
                    client.sendInfoMessage(response+" by "+ ch.getNickname());
                }
            }
        }
    }

    public synchronized void recieveAction(Action a){
        actions.add(a);
    }

    public ClientHandler clientHandlerOf(String nickname){
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
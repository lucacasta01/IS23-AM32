package it.polimi.myShelfie.server;

import it.polimi.myShelfie.model.Game;
import it.polimi.myShelfie.utilities.Utils;

import java.util.ArrayList;
import java.util.List;

public class Lobby implements Runnable{
    private final List<ClientHandler> lobbyPlayers;
    private String lobbyUID;
    private Integer playersNumber;
    private GameMode gameMode;
    private boolean isOpen;


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
        this.lobbyUID = lobbyUID;
        this.gameMode = GameMode.SAVEDGAME;
        this.isOpen = false;
    }

    @Override
    public void run() {
        switch (gameMode){
            case NEWGAME -> {
                Game game = new Game(lobbyUID,playersNumber);
                try {
                    broadcastMessage("Waiting for players..."+" "+"("+getLobbySize()+"/"+getPlayersNumber()+")");
                    waitForPlayers();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                broadcastMessage("Starting new game");

            }
            case SAVEDGAME -> {
                Game game = new Game(lobbyUID);
                playersNumber = game.getPlayersNumber();
                //read json from controller
                break;
            }
        }
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

    public boolean isOpen() {
        return isOpen;
    }

    public void acceptPlayer(ClientHandler player) throws RuntimeException{
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
            t.sendMessage(message);
        }
    }

    public void shutdown(){
        for(ClientHandler t : lobbyPlayers){
            t.shutdown();
        }

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
package it.polimi.myShelfie.controller;

import it.polimi.myShelfie.network.Lobby;
import it.polimi.myShelfie.model.Game;
import it.polimi.myShelfie.model.Player;
import it.polimi.myShelfie.model.Tile;
import it.polimi.myShelfie.model.cards.PersonalGoalCard;
import it.polimi.myShelfie.model.cards.SharedGoalCard;
import it.polimi.myShelfie.network.ClientHandler;
import it.polimi.myShelfie.utilities.ANSI;
import it.polimi.myShelfie.utilities.Position;
import it.polimi.myShelfie.utilities.Settings;
import it.polimi.myShelfie.utilities.pojo.Action;
import it.polimi.myShelfie.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameController {
    private Game game;
    private List<Tile> collectedTiles = new ArrayList<>();

    /**
     * Method used to pick up tiles from the game board
     * @param a action to execute
     */
    public String pickTiles(Action a){
        String nickname = a.getNickname();
        if(game.getPlayers().get(game.getCurrentPlayer()).getUsername().equals(nickname)){
            //your turn
            List<Position> tiles = a.getChosenTiles();
            collectedTiles = game.collectTiles(tiles);
            if (collectedTiles == null) {
                return "1";
            }else{
                StringBuilder builder = new StringBuilder();
                builder.append("picked tiles: ");
                for (Tile t : collectedTiles) {
                    builder.append(t.toString());
                    builder.append(" ");
                }
                return builder.toString();
            }
        }else{
            //not your turn
            return "0";
        }
    }

    /**
     * Method used to select in which column you want to insert the tiles you picked
     * @param a action
     */
    public String selectColumn(Action a){
        String nickname = a.getNickname();
        if(game.getPlayers().get(game.getCurrentPlayer()).getUsername().equals(nickname)){
            if(collectedTiles.size()==0){
                //no tiles selected
                return "2";
            }else{
                if(game.insertTiles(collectedTiles, a.getChosenColumn())){
                    collectedTiles.clear();
                    boolean res = this.game.getGameBoard().needToRefill();
                    if(res){
                        this.game.getGameBoard().initBoard(game.getPlayersNumber());
                    }
                    return "Tiles inserted correctly";
                }else{
                    //out of column limit
                    return "1";
                }
            }
        }else{
            //not your turn
            return "0";
        }
    }

    /**
     * Method used to select the order of insertion of the tiles
     * @param a action
     */
    public String orderTiles(Action a){
        String nickname = a.getNickname();
        if(game.getPlayers().get(game.getCurrentPlayer()).getUsername().equals(nickname)){
            if(collectedTiles!=null){
                if(collectedTiles.size()!=0){
                    String order = a.getInfo();
                    String[] colors = order.split(" ");
                    Tile[] tiles = new Tile[colors.length];
                    Tile toRemove = null;
                    for (int i = 0; i< colors.length; i++) {
                        String s = colors[i];
                        for (Tile t : collectedTiles) {
                            if (t.toString().equals(s)) {
                                toRemove = t;
                                tiles[i] = new Tile(t.getImagePath(), t.getColor());
                                break;
                            }
                        }
                        if(toRemove!=null){
                            collectedTiles.remove(toRemove);
                            toRemove=null;
                        }
                    }

                    this.collectedTiles.clear();
                    this.collectedTiles.addAll(Arrays.asList(tiles));
                    StringBuilder string = new StringBuilder();
                    string.append("Order changed successfully: ");
                    for (Tile t : this.collectedTiles) {
                        string.append(t.toString()).append(" ");
                    }
                    return string.toString();
                }else{
                    return "1";
                }
            }else{
                return "1";
            }
        }else{
            return "0";
        }
    }
    public ClientHandler[] reorderLobbyPlayers(List<ClientHandler> lobbyPlayers){
        ClientHandler[] oldGameOrder = new ClientHandler[lobbyPlayers.size()];
        for(int i = 0; i<lobbyPlayers.size(); i++){
            for(int j = 0; j<lobbyPlayers.size(); j++){
                if(lobbyPlayers.get(i).getNickname().equals(this.game.getOldGamePlayers().get(j).getUsername())){
                    oldGameOrder[j]=lobbyPlayers.get(i);
                }
            }
        }
        return oldGameOrder;
    }
    public void handleTurn(){
        this.game.handleTurn();
        this.game.saveGame();
    }
    public PersonalGoalCard drawPersonalGoal(){
        return this.game.drawPersonalGoal();
    }
    public boolean checkLastTurn(){
        return game.isLastTurn();
    }
    public List<Player> getGamePlayers(){
        return this.game.getPlayers();
    }
    public void setOldGamepLayers(){
        this.game.setPlayers(this.getOldGamePlayers());
    }
    public void setGamePlayers(List<Player> players){
        this.game.setPlayers(players);
    }
    public List<Player> getOldGamePlayers(){
        return this.game.getOldGamePlayers();
    }
    public void saveGame(){
        if(this.game!=null) {
            this.game.saveGame();
        }
    }

    public void setGameFinished() {
        this.game.setFinished(true);
    }

    /**
     * checks if the game has ended
     */
    public boolean isGameFinished(){
        if(game!=null) {
            return this.game.isFinished();
        }else{
            return false;
        }
    }

    public String getRank(boolean isGui){
        return game.getRank(isGui);
    }

    public View generateView(ClientHandler chToUpdate, Lobby lobby) {
        View view = new View();
        List<String> players = new ArrayList<>();
        List<String> GUIBoard = new ArrayList<>();
        List<List<String>> othersGUIShelves = new ArrayList<>();
        List<String> myShelf = new ArrayList<>();
        List<Integer> GUIScores = new ArrayList<>();
        List<String> GuiSharedCard = new ArrayList<>();
        List<String> shelves = new ArrayList<>();
        view.setANSIcolor(chToUpdate.getColor());
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
        view.setSharedCardsPointTokens(new ArrayList<>());
        for(SharedGoalCard s:game.getSharedDeck()){
            GuiSharedCard.add(s.getImgPath());
            view.getSharedCardsPointTokens().add(s.actualPointToken());
        }
        view.setGUIsharedCards(GuiSharedCard);


        //TUI shelves
        synchronized (game.getPlayers()) {
            for (Player p : game.getPlayers()) {
                shelves.add(lobby.clientHandlerOf(p.getUsername()).getColor() + p.getUsername() + ANSI.RESET_COLOR + ": " + p.getScore() + " points\n" + p.getMyShelf().toString());
            }
        }
        view.setShelves(shelves);

        List<String> sharedCards = new ArrayList<>();
        for(SharedGoalCard c: game.getSharedDeck()){
            sharedCards.add(c.toString());
        }
        view.setSharedCards(sharedCards);

        for(Player p:game.getPlayers()){
            if(!p.getUsername().equals(chToUpdate.getNickname())) {
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
        view.setCurPlayerGUIShelf(myShelf);
        view.setOthersGUIShelves(othersGUIShelves);
        view.setGUIScoring(GUIScores);
        view.setPlayers(players);
        view.setPersonalCard(game.chToPlayer(chToUpdate).getMyGoalCard().toString());
        view.setGUIpersonalCard(game.chToPlayer(chToUpdate).getMyGoalCard().getImgPath());
        return view;
    }

    /**
     * Performs endgame tasks to calculate points
     */
    public void endGameChecks() {
        for(Player p:game.getPlayers()){
            p.updateScore(p.getMyGoalCard().checkPersonalGoal(p.getMyShelf()));
            p.updateScore(p.getMyShelf().getShelfScore());
        }
    }

    /**
     * Performs game checks at the end of a turn
     */
    public String endTurnChecks(String nickname) {
        Player p=null;
        for(Player pl:game.getPlayers()){
            if(pl.getUsername().equals(nickname)){
                p=pl;
            }
        }
        List<SharedGoalCard> SharedDeck = game.getSharedDeck();
        String toReturn = "1";
        for (int i = 0; i<SharedDeck.size(); i++){
            SharedGoalCard c = SharedDeck.get(i);
            //System.out.println("checking shared goal: "+c.toString());
            if(!c.isAchieved(p)){
                if(c.checkPattern(p)){
                    toReturn ="Shared goal "+(i+1)+" achieved";
                    p.updateScore(c.popPointToken());
                }
            }
        }
        game.checkLastTurn(p);
        return toReturn;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public void addPlayer(List<Player> players) {
        game.addPlayer(players);
    }

    public List<Tile> getCollectedTiles() {
        return collectedTiles;
    }
}

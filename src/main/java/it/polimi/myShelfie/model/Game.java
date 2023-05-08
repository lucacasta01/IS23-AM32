package it.polimi.myShelfie.model;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.polimi.myShelfie.controller.ClientHandler;
import it.polimi.myShelfie.model.cards.*;
import it.polimi.myShelfie.utilities.*;
import it.polimi.myShelfie.utilities.beans.GameParameters;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Game implements Runnable{
    enum GameStatus{
        WAITINGFORPLAYERS,
        INPROGRESS,
        LASTITERATION,
        EXCEPTIONSTATUS,
        GAMEENDEDNORMALLY
    }
    private int playersNumber;
    private List<Player> players;
    private int currentPlayer;
    private Board gameBoard;
    private GameStatus status;
    private List<PersonalGoalCard> personalDeck;
    private List<SharedGoalCard> sharedDeck;
    private String UID;
    private boolean isFinished;
    private List<Player> oldGamePlayers;


    /**
     * Create a brand-new game
     * @param UID
     * @param playersNumber
     */
    public Game(String UID, int playersNumber){
        this.players = new ArrayList<>();
        this.gameBoard = new Board();
        this.playersNumber = playersNumber;
        this.currentPlayer = 0;
        this.UID = UID;
        this.isFinished = false;
        this.status = GameStatus.WAITINGFORPLAYERS;

        initializePersonalDeck();
        initializeSharedDeck(playersNumber);
        initBoard();
    }

    /**
     * Load a Game from the configuration file UID.json (if exists)
     * @param UID
     */
    public Game(String UID){
        this.UID = UID;
        this.isFinished = false;
        //load game by UID
        /*
        * JSON:
        *   UID
        *   PLAYER USERNAMES
        *   PLAYER SHELVES
        *   BOARD
        *   POINTS AND CARDS (FOR EACH PLAYER)
        *   SHARED CARDS
        */

        initializePersonalDeck();
        try{
            loadGame(UID);
        }
        catch (IOException e){
            throw new RuntimeException();
        }
    }

    public void saveGame(){
        GameParameters gameParameters = new GameParameters();

        gameParameters.setUID(this.UID);
        gameParameters.setBoard(this.gameBoard.toColorPosition());
        gameParameters.setCurrentPlayer(this.currentPlayer);

        for(Player p : players){
            gameParameters.addUsername(p.getUsername());
            gameParameters.addShelf(p.getMyShelf().toColorPosition());
            gameParameters.addScore(p.getScore());
            gameParameters.addPersonalCard(p.getMyGoalCard().getIndex());
        }

        for(SharedGoalCard c : sharedDeck){
            gameParameters.addSharedCard(c.getIndex());
        }

        for(Tile t : this.gameBoard.getTileHeap()){
            gameParameters.addTileToHeap(t);
        }

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        try {
            FileWriter fw = new FileWriter("src/config/savedgames/" + UID +".json");
            fw.write(gson.toJson(gameParameters));
            fw.close();
        }
        catch (IOException e){
            System.out.println(e.toString());
        }
    }

    public void loadGame(String UID) throws IOException{
        Path path = Paths.get("src/config/savedgames/" + UID +".json");

        if(!path.toFile().isFile()){
            throw new IOException();
        }

        GameParameters gameParameters = null;

        try {
                gameParameters = JsonParser.getGameParameters(path.toString());
        }catch (Exception e){
            System.out.println(e.toString());
        }

        this.UID = gameParameters.getUID();
        this.playersNumber = gameParameters.getUsernames().size();
        this.currentPlayer = gameParameters.getCurrentPlayer();
        this.gameBoard = loadBoard(gameParameters.getBoard(),gameParameters.getTileHeap());
        oldGamePlayers = new ArrayList<>();
        for(int i=0;i<playersNumber;i++){
            Player p = new Player(gameParameters.getUsernames().get(i));
            p.setScore(gameParameters.getScore().get(i));
            p.setMyShelf(loadShelf(gameParameters.getShelf().get(i)));
            p.setGoalCard(personalDeck.get(gameParameters.getPersonalCards().get(i)-1));
            oldGamePlayers.add(p);
        }
        initializeSharedDeck(gameParameters.getSharedCards().get(0),gameParameters.getSharedCards().get(1), playersNumber);

    }

    private Shelf loadShelf(List<ColorPosition> colorPositions){
        Tile[][] tileMatrix = new Tile[Constants.SHELFROW][Constants.SHELFCOLUMN];
        for(ColorPosition cp : colorPositions){
            tileMatrix[cp.getRow()][cp.getColumn()] = new Tile(cp.getImgpath(),cp.getTileColor());
        }
        return new Shelf(tileMatrix);
    }

    private Board loadBoard(List<ColorPosition> colorPositions, List<Tile> tileHeap){
        Tile[][] tileMatrix = new Tile[Constants.BOARD_DIM][Constants.BOARD_DIM];
        for(ColorPosition cp : colorPositions){
            tileMatrix[cp.getRow()][cp.getColumn()] = new Tile(cp.getImgpath(),cp.getTileColor());
        }
        return new Board(tileMatrix,tileHeap);
    }

    @Override
    public void run(){

    }

    public String getUID() {
        return UID;
    }
    public void initializePersonalDeck(){
        personalDeck = new ArrayList<>();
        try{
            for(int i=1;i<=12;i++){
                String jPath = new String("src/config/personalgoals/pg"+i+".json");
                PersonalGoalCard cardToAdd = generatePersonalGoalCard(JsonParser.getPersonalGoalConfig(jPath),i);
                personalDeck.add(cardToAdd);
            }
        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }

    private PersonalGoalCard generatePersonalGoalCard(List<ColorPosition> colorPositions, int n){
        List<Tile.Color> colors = new ArrayList<>();
        List<Position> positions = new ArrayList<>();
        for(ColorPosition c : colorPositions){
            colors.add(c.getTileColor());
            positions.add(new Position(c.getRow(),c.getColumn()));
        }
        PersonalGoalCard myCard = new PersonalGoalCard("graphics/personalGoalCards/Personal_Goals" + n + ".png");
        myCard.setPattern(positions,colors);
        return myCard;
    }

    private void initializeSharedDeck(int playerNumber){
        sharedDeck = new ArrayList<>();
        Random rnd = new Random();
        SharedGoalCard[] sharedGoalCards = new SharedGoalCard[12];
        sharedGoalCards[0] = new SharedGoal1Card("graphics/commonGoalCards/1.jpg", playerNumber);
        sharedGoalCards[1] = new SharedGoal2Card("graphics/commonGoalCards/2.jpg", playerNumber);
        sharedGoalCards[2] = new SharedGoal3Card("graphics/commonGoalCards/3.jpg", playerNumber);
        sharedGoalCards[3] = new SharedGoal4Card("graphics/commonGoalCards/4.jpg", playerNumber);
        sharedGoalCards[4] = new SharedGoal5Card("graphics/commonGoalCards/5.jpg", playerNumber);
        sharedGoalCards[5] = new SharedGoal6Card("graphics/commonGoalCards/6.jpg", playerNumber);
        sharedGoalCards[6] = new SharedGoal7Card("graphics/commonGoalCards/7.jpg", playerNumber);
        sharedGoalCards[7] = new SharedGoal8Card("graphics/commonGoalCards/8.jpg", playerNumber);
        sharedGoalCards[8] = new SharedGoal9Card("graphics/commonGoalCards/9.jpg", playerNumber);
        sharedGoalCards[9] = new SharedGoal10Card("graphics/commonGoalCards/10.jpg", playerNumber);
        sharedGoalCards[10] = new SharedGoal11Card("graphics/commonGoalCards/11.jpg", playerNumber);
        sharedGoalCards[11] = new SharedGoal12Card("graphics/commonGoalCards/12.jpg", playerNumber);

        int card1 = rnd.nextInt(0,11);
        int card2 = rnd.nextInt(0,11);
        while(card1 == card2){
            card2 = rnd.nextInt(0,11);
        }
        sharedDeck.add(sharedGoalCards[card1]);
        sharedDeck.add(sharedGoalCards[card2]);
    }

    private void initializeSharedDeck(int card1, int card2, int playerNumber){
        sharedDeck = new ArrayList<>();
        SharedGoalCard[] sharedGoalCards = new SharedGoalCard[12];
        sharedGoalCards[0] = new SharedGoal1Card("graphics/commonGoalCards/1.jpg", playerNumber);
        sharedGoalCards[1] = new SharedGoal2Card("graphics/commonGoalCards/2.jpg", playerNumber);
        sharedGoalCards[2] = new SharedGoal3Card("graphics/commonGoalCards/3.jpg", playerNumber);
        sharedGoalCards[3] = new SharedGoal4Card("graphics/commonGoalCards/4.jpg", playerNumber);
        sharedGoalCards[4] = new SharedGoal5Card("graphics/commonGoalCards/5.jpg", playerNumber);
        sharedGoalCards[5] = new SharedGoal6Card("graphics/commonGoalCards/6.jpg", playerNumber);
        sharedGoalCards[6] = new SharedGoal7Card("graphics/commonGoalCards/7.jpg", playerNumber);
        sharedGoalCards[7] = new SharedGoal8Card("graphics/commonGoalCards/8.jpg", playerNumber);
        sharedGoalCards[8] = new SharedGoal9Card("graphics/commonGoalCards/9.jpg", playerNumber);
        sharedGoalCards[9] = new SharedGoal10Card("graphics/commonGoalCards/10.jpg", playerNumber);
        sharedGoalCards[10] = new SharedGoal11Card("graphics/commonGoalCards/11.jpg", playerNumber);
        sharedGoalCards[11] = new SharedGoal12Card("graphics/commonGoalCards/12.jpg", playerNumber);

        sharedDeck.add(sharedGoalCards[card1-1]);
        sharedDeck.add(sharedGoalCards[card2-1]);
    }

    public int getPlayersNumber() {
        return playersNumber;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public void setGameBoard(Board gameBoard) {
        this.gameBoard = gameBoard;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public Board getGameBoard() {
        return gameBoard;
    }

    public GameStatus getStatus() {
        return status;
    }

    public void setPlayersNumber(int playersNumber) {
        this.playersNumber = playersNumber;
    }

    public void setCurrentPlayer(int currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    /**
     * Takes a random personal goal card from the initial deck and returns it removing it from the deck
     * @return the drawn card (PersonalGoalCard)
     */
    public PersonalGoalCard drawPersonalGoal(){
        int upperBound = this.personalDeck.size();
        Random rand = new Random();
        PersonalGoalCard toReturn = new PersonalGoalCard("emptycard");
        try {
            int myRand = rand.nextInt(upperBound);
            toReturn = this.personalDeck.get(myRand);
            this.personalDeck.remove(myRand);
        }
        catch (Exception e){
            System.out.println("Illegal action: PersonalDeck is empty.");
        }

        return toReturn;
    }

    /**
     * Takes a random shared goal card from the initial deck and returns it
     * @return the drawn card (SharedGoalCard)
     */
    public CheckSharedGoal drawSharedGoal(){
        int upperBound = this.sharedDeck.size();
        Random rand = new Random();
        int myRand = rand.nextInt(upperBound);
        CheckSharedGoal toReturn = this.sharedDeck.get(myRand);
        this.personalDeck.remove(myRand);
        return toReturn;
    }

    /**
     * Moves on with the game, giving the turn to the next player
     */
    public void handleTurn(){
        this.currentPlayer = (this.currentPlayer +1 ) % this.playersNumber;
    }

    public List<Tile> collectTiles(List<Position> tiles){
        if(tiles.size()==1){
            return this.collectTile(tiles.get(0));
        }else if(tiles.size()==2){
            return this.collectTile(tiles.get(0), tiles.get(1));
        }else if (tiles.size()==3){
            return this.collectTile(tiles.get(0), tiles.get(1), tiles.get(2));
        }
        return null;
    }
    /**
     * Takes a tile from the game board and returns it as a list of a single object
     * @return The said tile as a list of tiles
     */
    public List<Tile> collectTile(Position pos){
        if( this.gameBoard.isCatchable(pos.getRow(), pos.getColumn())) {
            Tile[][] currentGrid = this.gameBoard.getGrid();
            List<Tile> toReturn = new ArrayList<>();
            Tile t = currentGrid[pos.getRow()][pos.getColumn()];
            toReturn.add(new Tile(t.getImagePath(), t.getColor()));
            t.setColor(Tile.Color.NULLTILE);
            t.setImagePath("graphics/itemTiles/Transparent.png");
            return toReturn;
        }else{
            return null;
        }
    }

    /**
     * Takes two tiles from the game board and returns them in a list
     * @return a list containing the picked tiles
     */
    public List<Tile> collectTile(Position pos1, Position pos2){
        if(areCatchable(pos1, pos2)) {
            Tile[][] currentGrid = this.gameBoard.getGrid();
            List<Tile> toReturn = new ArrayList<>();
            Tile t1 = currentGrid[pos1.getRow()][pos1.getColumn()];
            Tile t2 = currentGrid[pos2.getRow()][pos2.getColumn()];
            toReturn.add(new Tile(t1.getImagePath(), t1.getColor()));
            toReturn.add(new Tile(t2.getImagePath(), t2.getColor()));
            t1.setColor(Tile.Color.NULLTILE);
            t1.setImagePath("graphics/itemTiles/Transparent.png");
            t2.setColor(Tile.Color.NULLTILE);
            t2.setImagePath("graphics/itemTiles/Transparent.png");
            return toReturn;
        }else{
            return null;
        }
    }

    /**
     * Takes three tiles from the game board and returns them in a list
     * @return a list containing the picked tiles
     */
    public List<Tile> collectTile(Position pos1, Position pos2, Position pos3){
        if(areCatchable(pos1, pos2, pos3)) {
            Tile[][] currentGrid = this.gameBoard.getGrid();
            List<Tile> toReturn = new ArrayList<>();
            Tile t1 = currentGrid[pos1.getRow()][pos1.getColumn()];
            Tile t2 = currentGrid[pos2.getRow()][pos2.getColumn()];
            Tile t3 = currentGrid[pos3.getRow()][pos3.getColumn()];
            toReturn.add(new Tile(t1.getImagePath(), t1.getColor()));
            toReturn.add(new Tile(t2.getImagePath(), t2.getColor()));
            toReturn.add(new Tile(t3.getImagePath(), t3.getColor()));
            t1.setColor(Tile.Color.NULLTILE);
            t1.setImagePath("graphics/itemTiles/Transparent.png");
            t2.setColor(Tile.Color.NULLTILE);
            t2.setImagePath("graphics/itemTiles/Transparent.png");
            t3.setColor(Tile.Color.NULLTILE);
            t3.setImagePath("graphics/itemTiles/Transparent.png");
            return toReturn;
        }else{
            return null;
        }
    }

    private boolean areCatchable(Position pos1, Position pos2){
        if(this.gameBoard.isCatchable(pos1.getRow(), pos1.getColumn())&&this.gameBoard.isCatchable(pos2.getRow(), pos2.getColumn())){
            if((pos1.getRow()==pos2.getRow())||(pos1.getColumn()==pos2.getColumn())){
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }

    private boolean areCatchable(Position pos1, Position pos2, Position pos3){
        if(this.gameBoard.isCatchable(pos1.getRow(), pos1.getColumn())&&this.gameBoard.isCatchable(pos2.getRow(), pos2.getColumn())&&this.gameBoard.isCatchable(pos3.getRow(), pos3.getColumn())){
            if(((pos1.getRow()== pos2.getRow())&&(pos2.getRow()==pos3.getRow()))||((pos1.getColumn()== pos2.getColumn())&&(pos2.getColumn()==pos3.getColumn()))){
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }



    /**
     * Takes a list of tiles as parameter and adds it inside the player's shelf by
     * looping the Shelf.insertTile method
     */
    public boolean insertTiles(List<Tile> toInsert, int column){
        Player current = this.players.get(currentPlayer);
        Shelf currentShelf = current.getMyShelf();
        if(currentShelf.freeTiles(column)>=toInsert.size()) {
            for (Tile t : toInsert) {
                currentShelf.insertTile(t, column);
            }
            return true;
        }else{
            return false;
        }
    }

    /**
     * Returns the final rank of the game
     * @return rank (String)
     */
    public String getRank(){
        List<Player> sortedPlayers = new ArrayList<>(players);
        sortedPlayers.sort(Comparator.comparingInt(Player::getScore).reversed());

        StringBuilder rank = new StringBuilder();
        rank.append(ANSI.BOLD).append("\t\t\t*** GAME RANK ***\n").append(ANSI.RESET_STYLE).append("\n");
        rank.append(ANSI.ITALIQUE).append("Position\t\tUsername\t\tScore\n");
        int pos = 1;
        for(int i=0;i<sortedPlayers.size();i++){
            if(i==0){
                rank.append(ANSI.GREEN);
            }
            rank.append(pos).append("\t\t\t\t").append(sortedPlayers.get(i).getUsername()).append("\t\t\t").append(sortedPlayers.get(i).getScore()).append("\n");
            int k = i;
            while(i<sortedPlayers.size()-1 && (sortedPlayers.get(i).getScore() == sortedPlayers.get(i+1).getScore())){
                rank.append(pos).append("\t\t\t\t").append(sortedPlayers.get(i+1).getUsername()).append("\t\t\t").append(sortedPlayers.get(i+1).getScore()).append("\n");
                i++;
            }
            if(k==0){
                rank.append(ANSI.RESET_COLOR);
            }
            pos++;
        }


        return rank.toString();
    }

    public List<SharedGoalCard> getSharedDeck() {
        return sharedDeck;
    }

    public List<Player> getOldGamePlayers() {
        return oldGamePlayers;
    }
    public Player chToPlayer(ClientHandler ch){
        for(Player p:players){
            if(p.getUsername().equals(ch.getNickname())){
                return p;
            }
        }
        return null;
    }

    /*
    public void checkSharedGoal(){
        Player current = this.players[this.getCurrentPlayer()];
        Shelf currentShelf = current.getMyShelf();
        for(CheckSharedGoal c : this.actualSharedGoal){
            c.checkPattern(current);
        }
    }
    */

    private void initBoard(){
        try {
            this.gameBoard.initBoard(this.playersNumber);
        }
        catch(Exception e){
            System.out.println(e.toString());
        }
    }

    public void addPlayer(Player p){
        if(players.size()<playersNumber) {
            players.add(p);
            p.setGoalCard(drawPersonalGoal());
            p.initShelf();
        }
        else{
            System.out.println("No more places available");
        }
    }

    public void addPlayer(List<Player> p){
        if(players.size() + p.size() <= playersNumber) {
            players.addAll(p);
            for(Player player : p) {
                player.setGoalCard(drawPersonalGoal());
                player.initShelf();
            }
        }
        else{
            System.out.println("No more places available");
        }
    }


}

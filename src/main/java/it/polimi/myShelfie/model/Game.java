package it.polimi.myShelfie.model;
import it.polimi.myShelfie.model.cards.*;
import it.polimi.myShelfie.utilities.ColorPosition;
import it.polimi.myShelfie.utilities.JsonParser;
import java.io.IOException;
import java.util.*;

public class Game {
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
    public List<CheckSharedGoal> sharedDeck;
    private List<CheckSharedGoal> actualSharedGoal;


    public Game(int nplayers){
        setPlayersNumber(nplayers);
        players = new ArrayList<>();
        setStatus(GameStatus.WAITINGFORPLAYERS);
        this.currentPlayer = 0;
        initializePersonalDeck();
        initializeSharedDeck();
        this.gameBoard = new Board();
        initBoard();
    }

    private void initializePersonalDeck(){
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

    private void initializeSharedDeck(){
        sharedDeck = new ArrayList<>();
        Random rnd = new Random();
        SharedGoalCard[] sharedGoalCards = new SharedGoalCard[12];
        sharedGoalCards[0] = new SharedGoal1Card("graphics/commonGoalCards/1.jpg");
        sharedGoalCards[1] = new SharedGoal2Card("graphics/commonGoalCards/2.jpg");
        sharedGoalCards[2] = new SharedGoal3Card("graphics/commonGoalCards/3.jpg");
        sharedGoalCards[3] = new SharedGoal4Card("graphics/commonGoalCards/4.jpg");
        sharedGoalCards[4] = new SharedGoal5Card("graphics/commonGoalCards/5.jpg");
        sharedGoalCards[5] = new SharedGoal6Card("graphics/commonGoalCards/6.jpg");
        sharedGoalCards[6] = new SharedGoal7Card("graphics/commonGoalCards/7.jpg");
        sharedGoalCards[7] = new SharedGoal8Card("graphics/commonGoalCards/8.jpg");
        sharedGoalCards[8] = new SharedGoal9Card("graphics/commonGoalCards/9.jpg");
        sharedGoalCards[9] = new SharedGoal10Card("graphics/commonGoalCards/10.jpg");
        sharedGoalCards[10] = new SharedGoal11Card("graphics/commonGoalCards/11.jpg");
        sharedGoalCards[11] = new SharedGoal12Card("graphics/commonGoalCards/12.jpg");

        int card1 = rnd.nextInt(0,11);
        int card2 = rnd.nextInt(0,11);
        while(card1 == card2){
            card2 = rnd.nextInt(0,11);
        }
        sharedDeck.add(sharedGoalCards[card1]);
        sharedDeck.add(sharedGoalCards[card2]);
    }

    public int getPlayersNumber() {
        return playersNumber;
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
     * Takes a random personal goal card from the initial deck and returns it
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

    /**
     * Takes a tile from the game board and returns it as a list of a single object
     * @return The said tile as a list of tiles
     */
    public List<Tile> collectTile(Position pos){
        assert this.gameBoard.isCatchable(pos.getRow(), pos.getColumn());
        Tile[][] currentGrid = this.gameBoard.getGrid();
        List<Tile> toReturn = new ArrayList<>();
        toReturn.add(currentGrid[pos.getRow()][pos.getColumn()]);
        currentGrid[pos.getRow()][pos.getColumn()].setColor(Tile.Color.NULLTILE);
        return toReturn;
    }

    /**
     * Takes two tiles from the game board and returns them in a list
     * @return a list containing the picked tiles
     */
    public List<Tile> collectTile(Position pos1, Position pos2){
        assert this.gameBoard.isCatchable(pos1.getRow(), pos1.getColumn());
        assert this.gameBoard.isCatchable(pos2.getRow(), pos2.getColumn());
        Tile[][] currentGrid = this.gameBoard.getGrid();
        List<Tile> toReturn = new ArrayList<>();
        toReturn.add(currentGrid[pos1.getRow()][pos1.getColumn()]);
        toReturn.add(currentGrid[pos2.getRow()][pos2.getColumn()]);
        currentGrid[pos1.getRow()][pos1.getColumn()].setColor(Tile.Color.NULLTILE);
        currentGrid[pos2.getRow()][pos2.getColumn()].setColor(Tile.Color.NULLTILE);
        return toReturn;
    }

    /**
     * Takes three tiles from the game board and returns them in a list
     * @return a list containing the picked tiles
     */
    public List<Tile> collectTile(Position pos1, Position pos2, Position pos3){
        assert this.gameBoard.isCatchable(pos1.getRow(), pos1.getColumn());
        assert this.gameBoard.isCatchable(pos2.getRow(), pos2.getColumn());
        assert this.gameBoard.isCatchable(pos3.getRow(), pos3.getColumn());
        Tile[][] currentGrid = this.gameBoard.getGrid();
        List<Tile> toReturn = new ArrayList<>();
        toReturn.add(currentGrid[pos1.getRow()][pos1.getColumn()]);
        toReturn.add(currentGrid[pos2.getRow()][pos2.getColumn()]);
        toReturn.add(currentGrid[pos3.getRow()][pos3.getColumn()]);
        currentGrid[pos1.getRow()][pos1.getColumn()].setColor(Tile.Color.NULLTILE);
        currentGrid[pos2.getRow()][pos2.getColumn()].setColor(Tile.Color.NULLTILE);
        currentGrid[pos3.getRow()][pos3.getColumn()].setColor(Tile.Color.NULLTILE);
        return toReturn;
    }

    /**
     * Takes a list of tiles as parameter and adds it inside the player's shelf by
     * looping the Shelf.insertTile method
     */
    public void insertTile(List<Tile> toInsert, int column){
        Player current = this.players.get(currentPlayer);
        Shelf currentShelf = current.getMyShelf();
        for(Tile t : toInsert) {
            currentShelf.insertTile(t, column);
        }
    }

    /**
     * Returns the Player object of the winner
     * @return winner (Player)
     */
    public Player getWinner(){
        int max = 0;
        Player winner = this.players.get(0);
        for(Player p : this.players){
            if(p.getScore() >= max){
                max = p.getScore();
                winner = p;
            }
        }
        return winner;

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
        }
        else{
            System.out.println("No more places available");
        }
    }


}

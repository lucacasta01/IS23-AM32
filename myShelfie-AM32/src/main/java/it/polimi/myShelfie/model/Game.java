package it.polimi.myShelfie.model;

import it.polimi.myShelfie.model.cards.PersonalGoalCard;
import it.polimi.myShelfie.model.cards.CheckSharedGoal;
import it.polimi.myShelfie.model.cards.SharedGoalCard;

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
    private Player[] players;
    private int currentPlayer;
    private Board gameBoard;
    private GameStatus status;
    private List<PersonalGoalCard> personalDeck;
    private List<CheckSharedGoal> sharedDeck;
    private List<CheckSharedGoal> actualSharedGoal;


    public Game(int nplayers){
        setPlayersNumber(nplayers);
        setStatus(GameStatus.WAITINGFORPLAYERS);
        this.currentPlayer = 0;
        initializePersonalDeck();
        initializeSharedDeck();
        this.gameBoard = new Board();
        initBoard();
    }

    private void initializePersonalDeck(){
        // does something
    }

    private void initializeSharedDeck(){
        // does something
    }

    private void initializeTilesHeap(){
        // does something
    }
    
    public int getPlayersNumber() {
        return playersNumber;
    }

    public Player[] getPlayers() {
        return players;
    }

    public void setPlayers(Player[] players) {
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
        int myRand = rand.nextInt(upperBound);
        PersonalGoalCard toReturn = this.personalDeck.get(myRand);
        this.personalDeck.remove(myRand);
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
    public List<Tile> collectTIle(Position pos){
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
        Player current = this.players[currentPlayer];
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
        Player winner = this.players[0];
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


}

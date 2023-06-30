package it.polimi.myShelfie.utilities.pojo;
import it.polimi.myShelfie.model.Tile;
import it.polimi.myShelfie.utilities.ColorPosition;
import java.util.ArrayList;
import java.util.List;

/**
 * represents game parameters needed to load a game from disk
 */
public class GameParameters {
    /*
     * JSON:
     *   UID
     *   PLAYER USERNAMES
     *   PLAYER SHELVES
     *   BOARD
     *   TILEHEAP
     *   POINTS AND CARDS (FOR EACH PLAYER)
     */

    private String UID;
    private List<String> usernames;
    private List<List<ColorPosition>> shelf;
    private List<ColorPosition> board;
    private List<Integer> score;
    private List<Integer> personalCards;
    private List<Integer> sharedCards;
    private Integer currentPlayer;
    private final List<Tile> tileHeap;
    private boolean isLastTurn;

    public GameParameters(){
        usernames = new ArrayList<>();
        shelf = new ArrayList<>();
        score = new ArrayList<>();
        personalCards = new ArrayList<>();
        sharedCards = new ArrayList<>();
        tileHeap = new ArrayList<>();
    }

    public boolean isLastTurn() {
        return isLastTurn;
    }

    public void setLastTurn(boolean lastTurn) {
        isLastTurn = lastTurn;
    }

    public void addTileToHeap(Tile t){
        this.tileHeap.add(t);
    }

    public List<Tile> getTileHeap() {
        return tileHeap;
    }

    public void setCurrentPlayer(Integer currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public Integer getCurrentPlayer() {
        return currentPlayer;
    }

    public void setUsernames(List<String> usernames) {
        this.usernames = usernames;
    }

    public void setShelf(List<List<ColorPosition>> shelf) {
        this.shelf = shelf;
    }

    public void setScore(List<Integer> score) {
        this.score = score;
    }

    public void setPersonalCards(List<Integer> personalCards) {
        this.personalCards = personalCards;
    }

    public void setSharedCards(List<Integer> sharedCards) {
        this.sharedCards = sharedCards;
    }

    public String getUID() {
        return UID;
    }

    public List<String> getUsernames() {
        return usernames;
    }

    public List<List<ColorPosition>> getShelf() {
        return shelf;
    }

    public List<ColorPosition> getBoard() {
        return board;
    }

    public List<Integer> getScore() {
        return score;
    }

    public List<Integer> getPersonalCards() {
        return personalCards;
    }

    public List<Integer> getSharedCards() {
        return sharedCards;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public void addUsername(String username) {
        this.usernames.add(username);
    }

    public void addShelf(List<ColorPosition> shelf) {
        this.shelf.add(shelf);
    }

    public void setBoard(List<ColorPosition> board) {
        this.board = board;
    }

    public void addScore(Integer score) {
        this.score.add(score);
    }

    public void addPersonalCard(Integer personalCards) {
        this.personalCards.add(personalCards);
    }

    public void addSharedCard(Integer sharedCard) {
        this.sharedCards.add(sharedCard);
    }
}
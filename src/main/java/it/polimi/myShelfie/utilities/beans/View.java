package it.polimi.myShelfie.utilities.beans;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
public class View implements Serializable {
    private String board;
    private List<String> shelves;
    private List<String> sharedCards;
    private String personalCard;
    private String currentPlayer;



    public View() {
        this.shelves = new ArrayList<>();
        this.sharedCards = new ArrayList<>();
    }
    public List<String> getSharedCards() {
        return sharedCards;
    }

    public String getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(String currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public String getPersonalCard() {
        return personalCard;
    }

    public void setSharedCards(List<String> sharedCards) {
        this.sharedCards = sharedCards;
    }

    public void setPersonalCard(String personalCard) {
        this.personalCard = personalCard;
    }

    public void setBoard(String board) {
        this.board = board;
    }

    public void setShelves(List<String> shelves) {
        this.shelves = shelves;
    }

    public List<String> getShelves() {
        return shelves;
    }

    public String getBoard() {
        return board;
    }
    public void addShelf(String Shelf){
        this.shelves.add(Shelf);
    }
}

package it.polimi.myShelfie.utilities.beans;

import java.util.ArrayList;
import java.util.List;

public class View {
    private String board;
    private List<String> shelves;
    private List<String> sharedCards;
    private String personalCard;



    public View() {
        this.shelves = new ArrayList<>();
        this.sharedCards = new ArrayList<>();
    }
    public List<String> getSharedCards() {
        return sharedCards;
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

package it.polimi.myShelfie.utilities.beans;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class View implements Serializable {
    private String board;
    private List<String> shelves;
    private List<String> sharedCards;
    private String personalCard;
    private String currentPlayer;

    private Map<String, List<String>> GUIplayersAndShelves;
    private Map<String, Integer> GUIplayersAndScoring;
    private List<String> GUIboard;
    private List<String> GUIsharedCards;
    private String GUIpersonalCard;
    public View() {
        this.shelves = new ArrayList<>();
        this.sharedCards = new ArrayList<>();
        this.GUIboard = new ArrayList<>();
        this.GUIplayersAndShelves = new HashMap<>();
        this.GUIplayersAndScoring = new HashMap<>();
        this.GUIsharedCards = new ArrayList<>();
    }

    public String getGUIpersonalCard() {
        return GUIpersonalCard;
    }

    public void setGUIpersonalCard(String GUIpersonalCard) {
        this.GUIpersonalCard = GUIpersonalCard;
    }

    public void setGUIplayersAndShelves(Map<String, List<String>> GUIplayersAndShelves) {
        this.GUIplayersAndShelves = GUIplayersAndShelves;
    }

    public void setGUIplayersAndScoring(Map<String, Integer> GUIplayersAndScoring) {
        this.GUIplayersAndScoring = GUIplayersAndScoring;
    }

    public void setGUIboard(List<String> GUIboard) {
        this.GUIboard = GUIboard;
    }

    public void setGUIsharedCards(List<String> GUIsharedCards) {
        this.GUIsharedCards = GUIsharedCards;
    }

    public Map<String, List<String>> getGUIplayersAndShelves() {
        return GUIplayersAndShelves;
    }

    public Map<String, Integer> getGUIplayersAndScoring() {
        return GUIplayersAndScoring;
    }

    public List<String> getGUIboard() {
        return GUIboard;
    }

    public List<String> getGUIsharedCards() {
        return GUIsharedCards;
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

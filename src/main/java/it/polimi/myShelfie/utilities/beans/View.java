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
    private List<String> players;
    private List<List<String>> othersGUIShelves;
    private List<String> myShelf;
    private List<Integer> GUIScoring;
    private List<String> GUIboard;
    private List<String> GUIsharedCards;
    private String GUIpersonalCard;
    private String ANSIcolor;
    public View() {
        this.shelves = new ArrayList<>();
        this.sharedCards = new ArrayList<>();
        this.GUIboard = new ArrayList<>();
        this.othersGUIShelves = new ArrayList<>();
        this.GUIScoring = new ArrayList<>();
        this.players = new ArrayList<>();
        this.GUIsharedCards = new ArrayList<>();
        this.myShelf = new ArrayList<>();
    }

    public List<List<String>> getOthersGUIShelves() {
        return othersGUIShelves;
    }

    public List<String> getMyShelf() {
        return myShelf;
    }



    public void setMyShelf(List<String> myShelf) {
        this.myShelf = myShelf;
    }

    public String getGUIpersonalCard() {
        return GUIpersonalCard;
    }

    public void setGUIpersonalCard(String GUIpersonalCard) {
        this.GUIpersonalCard = GUIpersonalCard;
    }

    public List<String> getPlayers() {
        return players;
    }

    public void setPlayers(List<String> players) {
        this.players = players;
    }

    public void setOthersGUIShelves(List<List<String>> GUIShelves) {
        this.othersGUIShelves = GUIShelves;
    }

    public void setGUIScoring(List<Integer> GUIScoring) {
        this.GUIScoring = GUIScoring;
    }

    public void setGUIboard(List<String> GUIboard) {
        this.GUIboard = GUIboard;
    }

    public void setGUIsharedCards(List<String> GUIsharedCards) {
        this.GUIsharedCards = GUIsharedCards;
    }

    public List<List<String>> getGUIShelves() {
        return othersGUIShelves;
    }

    public List<Integer> getGUIScoring() {
        return GUIScoring;
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

    public void setANSIcolor(String ANSIcolor) {
        this.ANSIcolor = ANSIcolor;
    }

    public String getANSIcolor() {
        return ANSIcolor;
    }
}

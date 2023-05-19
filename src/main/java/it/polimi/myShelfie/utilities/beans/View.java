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
    private List<String> players;
    private List<List<String>> GUIShelves;
    private List<Integer> GUIScoring;
    private List<String> GUIboard;
    private List<String> GUIsharedCards;
    private String GUIpersonalCard;
    private String ANSIcolor;
    public View() {
        this.shelves = new ArrayList<>();
        this.sharedCards = new ArrayList<>();
        this.GUIboard = new ArrayList<>();
        this.GUIShelves = new ArrayList<>();
        this.GUIScoring = new ArrayList<>();
        this.players = new ArrayList<>();
        this.GUIsharedCards = new ArrayList<>();
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

    public void setGUIShelves(List<List<String>> GUIShelves) {
        this.GUIShelves = GUIShelves;
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
        return GUIShelves;
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

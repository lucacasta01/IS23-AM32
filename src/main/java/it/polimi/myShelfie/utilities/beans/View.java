package it.polimi.myShelfie.utilities.beans;

import java.util.ArrayList;
import java.util.List;

public class View {
    private String board;
    private List<String> shelves;

    public View() {
        this.shelves = new ArrayList<>();
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

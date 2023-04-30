package it.polimi.myShelfie.model;

import it.polimi.myShelfie.utilities.ColorPosition;
import it.polimi.myShelfie.utilities.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Shelf {
    private Tile[][] tileMatrix;

    public Shelf() {
        this.tileMatrix = new Tile[Constants.SHELFROW][Constants.SHELFCOLUMN];
        initShelf();
    }

    public Shelf(Tile[][] tileMatrix){
        this.tileMatrix = tileMatrix;
    }
    /**
     * initialize the shelf with NULLTILE type tiles
     */
    public void initShelf(){
        for(int i = 0; i<Constants.SHELFROW; i++){
            for(int j = 0; j<Constants.SHELFCOLUMN; j++){
                this.tileMatrix[i][j] = new Tile("test", Tile.Color.NULLTILE);
            }
        }
    }
    /**
     * @return a copy of this object Tile[6][5] tileMatrix
     */
    public Tile[][] getTileMartrix() {
        Tile[][] toReturn = new Tile[Constants.SHELFROW][];
        for(int i=0;i<Constants.SHELFROW;i++){
            toReturn[i] = this.tileMatrix[i].clone();
        }
        return toReturn;
    }

    /**
     * Checks whether the shelf is full
     * @return true if is full, false otherwise
     */
    private boolean checkIsFull(){
        boolean check = true;
        for(int i=0; i<Constants.SHELFROW; i++){
            for(int j=0; j<Constants.SHELFCOLUMN; j++){
                if(tileMatrix[i][j].getColor()==Tile.Color.NULLTILE){
                    return false;
                }
            }

        }
        return true;
    }

    /**
     * Checks the number of free spaces in a column
     * @param column is the column to be checked
     * @return the number of free spaces in the given column
     */
    public int freeTiles(int column){
        int free = 0;
        for(int i=0; i<Constants.SHELFROW; i++){
            if(this.tileMatrix[i][column].getColor()!=Tile.Color.NULLTILE){
                return free;
            }else{
                free++;
            }
        }
        return free;
    }

    /**
     * Checks every column, calculate the maximum number of tiles that the player can insert
     * @return the maximum number of tiles that can be putted in the board
     */
    public int maxInsert(){
        int act = 0, max = 0;
        for(int i = 0; i<Constants.SHELFCOLUMN; i++){
            for(int j=0; j<Constants.SHELFROW; j++){
                if(tileMatrix[j][i].getColor()==Tile.Color.NULLTILE){
                    act++;
                }
            }
            if(act>max){
                max=act;
            }
            act = 0;
        }
        return Math.min(max, 3);
    }

    /**
     * insert the given tile in the first free position from the bottom in the given column
     */
    public void insertTile(Tile t, int column) {
        for (int i = Constants.SHELFROW - 1; i >= 0; i--) {
            if (tileMatrix[i][column].getColor() == Tile.Color.NULLTILE) {
                tileMatrix[i][column] = t;
                return;
            }
        }
    }

    public List<ColorPosition> toColorPosition(){
        List<ColorPosition> toReturn = new ArrayList<>();
        for(int i=0;i<Constants.SHELFROW;i++){
            for(int j=0;j<Constants.SHELFCOLUMN;j++){
                toReturn.add(new ColorPosition(tileMatrix[i][j].toString(),tileMatrix[i][j].getImagePath(),i,j));
            }
        }

        return toReturn;
    }

    @Override
    public String toString(){
        StringBuilder s = new StringBuilder();
        for(int i=0;i<Constants.SHELFROW;i++){
            for(int j=0;j<Constants.SHELFCOLUMN;j++){
                s.append(tileMatrix[i][j].toString()).append("\t");
            }
            s.append("\n");
        }
        for(int i=0;i<Constants.SHELFCOLUMN;i++){
            s.append(i+1).append("\t");
        }
        s.append("\n");
        return s.toString();
    }
}

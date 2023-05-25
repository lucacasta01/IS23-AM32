package it.polimi.myShelfie.model;
import it.polimi.myShelfie.utilities.ANSI;
import it.polimi.myShelfie.utilities.ColorPosition;
import it.polimi.myShelfie.utilities.Settings;
import java.util.ArrayList;
import java.util.List;
public class Shelf {
    private Tile[][] tileMatrix;

    public Shelf() {
        this.tileMatrix = new Tile[Settings.SHELFROW][Settings.SHELFCOLUMN];
        initShelf();
    }

    public Shelf(Tile[][] tileMatrix){
        this.tileMatrix = tileMatrix;
    }
    /**
     * initialize the shelf with NULLTILE type tiles
     */
    public void initShelf(){
        for(int i = 0; i< Settings.SHELFROW; i++){
            for(int j = 0; j< Settings.SHELFCOLUMN; j++){
                this.tileMatrix[i][j] = new Tile("/graphics/itemTiles/transparent.png", Tile.Color.NULLTILE);
            }
        }
    }
    /**
     * @return a copy of this object Tile[6][5] tileMatrix
     */
    public Tile[][] getTileMartrix() {
        Tile[][] toReturn = new Tile[Settings.SHELFROW][];
        for(int i = 0; i< Settings.SHELFROW; i++){
            toReturn[i] = this.tileMatrix[i].clone();
        }
        return toReturn;
    }

    /**
     * Checks whether the shelf is full
     * @return true if is full, false otherwise
     */
    public boolean checkIsFull(){
        for(int i = 0; i< Settings.SHELFROW; i++){
            for(int j = 0; j< Settings.SHELFCOLUMN; j++){
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
        for(int i = 0; i< Settings.SHELFROW; i++){
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
        for(int i = 0; i< Settings.SHELFCOLUMN; i++){
            for(int j = 0; j< Settings.SHELFROW; j++){
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
        for (int i = Settings.SHELFROW - 1; i >= 0; i--) {
            if (tileMatrix[i][column].getColor() == Tile.Color.NULLTILE) {
                tileMatrix[i][column] = t;
                return;
            }
        }
    }

    public List<ColorPosition> toColorPosition(){
        List<ColorPosition> toReturn = new ArrayList<>();
        for(int i = 0; i< Settings.SHELFROW; i++){
            for(int j = 0; j< Settings.SHELFCOLUMN; j++){
                toReturn.add(new ColorPosition(tileMatrix[i][j].toString(),tileMatrix[i][j].getImagePath(),i,j));
            }
        }

        return toReturn;
    }

    @Override
    public String toString(){
        StringBuilder s = new StringBuilder();
        for(int i = 0; i< Settings.SHELFROW; i++){
            for(int j = 0; j< Settings.SHELFCOLUMN; j++){
                s.append(tileMatrix[i][j].toString()).append("\t");
            }
            s.append("\n");
        }
        s.append(ANSI.ITALIC).append(ANSI.BOLD);
        for(int i = 0; i< Settings.SHELFCOLUMN; i++){
            s.append(i+1).append("\t");
        }
        s.append(ANSI.RESET_STYLE);
        s.append("\n");
        return s.toString();
    }

    public List<Integer> getColorClusterSizes() {
        Tile[][] matrix = getTileMartrix();
        List<Integer> clusterSizes = new ArrayList<>();
        boolean[][] visited = new boolean[Settings.SHELFROW][Settings.SHELFCOLUMN];

        for (int i = 0; i < Settings.SHELFROW; i++) {
            for (int j = 0; j < Settings.SHELFCOLUMN; j++) {
                if (!visited[i][j]) {
                    int clusterSize = depthFirstSearch(i, j, matrix, visited, matrix[i][j].getColor());
                    if (clusterSize > 0) {
                        clusterSizes.add(clusterSize);
                    }
                }
            }
        }

        return clusterSizes;
    }

    /**
     * Method used to explore a matrix via depth first search, it's basically the same as exploring a tree
     */
    private int depthFirstSearch(int i, int j, Tile[][] matrix, boolean[][] visited, Tile.Color color) {
        if (i < 0 || i >= matrix.length || j < 0 || j >= matrix[0].length) {
            return 0;
        }

        if (visited[i][j] || matrix[i][j].getColor() != color || matrix[i][j].getColor() == Tile.Color.NULLTILE
        || matrix[0][0].getColor() == Tile.Color.NULLTILE) {
            return 0;
        }

        visited[i][j] = true;

        int size = 1;
        size += depthFirstSearch(i - 1, j, matrix, visited, matrix[i][j].getColor());
        size += depthFirstSearch(i + 1, j, matrix, visited, matrix[i][j].getColor());
        size += depthFirstSearch(i, j - 1, matrix, visited, matrix[i][j].getColor());
        size += depthFirstSearch(i, j + 1, matrix, visited, matrix[i][j].getColor());

        return size;
    }

    public int getShelfScore(){
        List<Integer> clusterSizes = getColorClusterSizes();
        int toReturn = 0;
        for(int s : clusterSizes){
            if(s > 2){
                switch(s){
                    case 3: toReturn += 2;
                    break;
                    case 4: toReturn += 3;
                    break;
                    case 5: toReturn += 5;
                    break;
                    default: toReturn += 8;
                    break;
                }
            }
        }
        return toReturn;
    }

}

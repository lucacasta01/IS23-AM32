package it.polimi.myShelfie.model;
import it.polimi.myShelfie.utilities.ColorPosition;
import it.polimi.myShelfie.utilities.Settings;
import it.polimi.myShelfie.utilities.JsonParser;
import it.polimi.myShelfie.utilities.Position;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
public class Board {
    private final Tile[][] grid;
    private List<Tile> tileHeap;


    public Board() {
        this.grid = new Tile[Settings.BOARD_DIM][Settings.BOARD_DIM];
        for(int i = 0; i< Settings.BOARD_DIM; i++){
            for(int j = 0; j< Settings.BOARD_DIM; j++){
                this.grid[i][j] = null;
            }
        }
        initTileHeap();
    }


    public Board(Tile[][] tileMatrix, List<Tile> tileHeap) {
        this.grid = tileMatrix;
        this.tileHeap = tileHeap;
    }

    /**
     *
     * @return current state of the board's grid
     */
    public Tile[][] getGrid() {
        return this.grid;
    }

    /**
     * @return current state of the tile's heap
     */
    public List<Tile> getTileHeap() {
        return tileHeap;
    }

    public String getTileHeapToString(){
        StringBuilder toReturn = new StringBuilder();
        for(Tile t : tileHeap){
            toReturn.append(t.toString()).append("\n");
        }
        return toReturn.toString();
    }

    /**
     * Checks whether the board's grid needs to be refilled
     * @return true if needed, false otherwise
     */
    public boolean needToRefill() {
        boolean check = true;
        for (int i = 0; i < Settings.BOARD_DIM; i++) {
            for (int j = 0; j < Settings.BOARD_DIM; j++) {
                Tile toCheck = this.grid[i][j];
                if (this.grid[i][j].getColor() != Tile.Color.NULLTILE) {
                    if (((this.grid[Math.min(i + 1, Settings.BOARD_DIM-1)][j].getColor() != Tile.Color.NULLTILE) &&  this.grid[Math.min(i + 1, Settings.BOARD_DIM-1)][j] != toCheck) ||
                            ((this.grid[i][Math.min(j + 1, Settings.BOARD_DIM-1)].getColor() != Tile.Color.NULLTILE) && this.grid[i][Math.min(j + 1, Settings.BOARD_DIM-1)] != toCheck) ||
                            ((this.grid[Math.max(i - 1, 0)][j].getColor() != Tile.Color.NULLTILE) && this.grid[Math.max(i - 1, 0)][j] != toCheck) ||
                            (( this.grid[i][Math.max(j - 1, 0)].getColor() != Tile.Color.NULLTILE) && this.grid[i][Math.max(j - 1, 0)] != toCheck)) {
                        check = false;
                    }
                }
            }
        }
        return check;
    }

    /**
     * Checks if a Tile in a given position is catchable by a player
     *
     * @param row    of the tile we want to catch
     * @param column of the tile we want to catch
     * @return true if the Tile is catchable, else otherwise
     */
    public boolean isCatchable(int row, int column) {
        if (this.grid[row][column].getColor() == Tile.Color.NULLTILE) {
            return false;
        }
        Tile toCheck = this.grid[row][column];
        // might need to be modified
        return  ((this.grid[Math.min(row + 1, Settings.BOARD_DIM)][column].getColor() == Tile.Color.NULLTILE) &&  this.grid[Math.min(row + 1, Settings.BOARD_DIM)][column] != toCheck) ||
                ((this.grid[row][Math.min(column + 1, Settings.BOARD_DIM)].getColor() == Tile.Color.NULLTILE) && this.grid[row][Math.min(column + 1, Settings.BOARD_DIM)] != toCheck) ||
                ((this.grid[Math.max(row - 1, 0)][column].getColor() == Tile.Color.NULLTILE) && this.grid[Math.max(row - 1, 0)][column] != toCheck) ||
                (( this.grid[row][Math.max(column - 1, 0)].getColor() == Tile.Color.NULLTILE) && this.grid[row][Math.max(column - 1, 0)] != toCheck);
    }

    /**
     * Initializes the tiles heap with relative colors and image path
     */
    private void initTileHeap(){
        this.tileHeap = new ArrayList<>();
        for(int i = 0; i< Settings.TILES_GROUP; i++){
            if(i<8){
                tileHeap.add(new Tile("/graphics/itemTiles/Cornici1.3.png",Tile.Color.BLUE));
            }
            else if(i<15){
                tileHeap.add(new Tile("/graphics/itemTiles/Cornici1.2.png",Tile.Color.BLUE));
            }
            else{
                tileHeap.add(new Tile("/graphics/itemTiles/Cornici1.1.png",Tile.Color.BLUE));
            }
        }
        for(int i = 0; i< Settings.TILES_GROUP; i++){
            if(i<8){
                tileHeap.add(new Tile("/graphics/itemTiles/Gatti1.2.png",Tile.Color.GREEN));
            }
            else if(i<15){
                tileHeap.add(new Tile("/graphics/itemTiles/Gatti1.1.png",Tile.Color.GREEN));
            }
            else{
                tileHeap.add(new Tile("/graphics/itemTiles/Gatti1.3.png",Tile.Color.GREEN));
            }
        }
        for(int i = 0; i< Settings.TILES_GROUP; i++){
            if(i<8){
                tileHeap.add(new Tile("/graphics/itemTiles/Giochi1.2.png",Tile.Color.YELLOW));
            }
            else if(i<15){
                tileHeap.add(new Tile("/graphics/itemTiles/Giochi1.3.png",Tile.Color.YELLOW));
            }
            else{
                tileHeap.add(new Tile("/graphics/itemTiles/Giochi1.1.png",Tile.Color.YELLOW));
            }
        }
        for(int i = 0; i< Settings.TILES_GROUP; i++){
            if(i<8){
                tileHeap.add(new Tile("/graphics/itemTiles/Libri1.3.png",Tile.Color.WHITE));
            }
            else if(i<15){
                tileHeap.add(new Tile("/graphics/itemTiles/Libri1.2.png",Tile.Color.WHITE));
            }
            else{
                tileHeap.add(new Tile("/graphics/itemTiles/Libri1.1.png",Tile.Color.WHITE));
            }
        }
        for(int i = 0; i< Settings.TILES_GROUP; i++){
            if(i<8){
                tileHeap.add(new Tile("/graphics/itemTiles/Piante1.3.png",Tile.Color.PINK));
            }
            else if(i<15){
                tileHeap.add(new Tile("/graphics/itemTiles/Piante1.2.png",Tile.Color.PINK));
            }
            else{
                tileHeap.add(new Tile("/graphics/itemTiles/Piante1.1.png",Tile.Color.PINK));
            }
        }
        for(int i = 0; i< Settings.TILES_GROUP; i++){
            if(i<8){
                tileHeap.add(new Tile("/graphics/itemTiles/Trofei1.1.png",Tile.Color.LIGHTBLUE));
            }
            else if(i<15){
                tileHeap.add(new Tile("/graphics/itemTiles/Trofei1.2.png",Tile.Color.LIGHTBLUE));
            }
            else{
                tileHeap.add(new Tile("/graphics/itemTiles/Trofei1.3.png",Tile.Color.LIGHTBLUE));
            }
        }
    }

    /**
     * Casually picks a tile from the tileHeap then deletes the picked tile from the heap
     * @return the randomly picked tile
     */
    private Tile pickTile(){
        if(tileHeap.size()!=0) {
            Tile t = tileHeap.get(new Random().nextInt(0, tileHeap.size() - 1));
            tileHeap.remove(t);
            return t;
        }else{
            return new Tile("/graphics/itemTiles/transparent.png", Tile.Color.NULLTILE);
        }
    }

    /**
     * initializes the board with all the necessary tiles
     * @param players is the game's number of players
     */
    public void initBoard(int players){
    for(int i = 0; i< Settings.BOARD_DIM; i++){
        for(int j = 0; j< Settings.BOARD_DIM; j++){
            if(this.grid[i][j]!=null&&this.grid[i][j].getColor()== Tile.Color.NULLTILE){
                this.grid[i][j]=null;
            }
        }
}
        try {
            setNullTiles(JsonParser.getNullConfig("/configJsons/boardconfig.json"));
            switch (players) {
                case 2 -> setNullTiles(JsonParser.getNullConfig("/configJsons/boardconfig2p.json"));
                case 3 -> setNullTiles(JsonParser.getNullConfig("/configJsons/boardconfig3p.json"));
            }
            setTileColors();
        }
        catch (IOException e){
            System.out.println("JSON parsing error");
        }
    }

    /**
     * pick tiles from the heap and assign them to their relative place
     */
    private void setTileColors(){
        for(int i = 0; i< Settings.BOARD_DIM; i++){
            for(int j = 0; j< Settings.BOARD_DIM; j++){
                if(grid[i][j] == null){
                    grid[i][j] = pickTile();
                }
            }
        }
    }

    /**
     * sets all out of borders tiles
     * @param nullTiles list of all the out of border positions
     */
    private void setNullTiles(List<Position> nullTiles){
        for(Position p : nullTiles){
            grid[p.getRow()][p.getColumn()] = new Tile("/graphics/itemTiles/transparent.png", Tile.Color.NULLTILE);
        }
    }


    @Override
    public String toString(){
        StringBuilder s = new StringBuilder();
        s.append("\t");
        for(int i = 0; i< Settings.BOARD_DIM; i++)
            s.append(i+1).append(" | ");
        s.append("\n");
        for(int i = 0; i< Settings.BOARD_DIM; i++){
            s.append(i+1).append("\t");
            for(int j = 0; j< Settings.BOARD_DIM; j++){
                s.append(grid[i][j].toString()).append(" | ");
            }
            s.append("\n");
        }
        return s.toString();
    }

    /**
     * Generates a list of ColorPositions starting from the board
     * @return List of positions
     */
    public List<ColorPosition> toColorPosition(){
        List<ColorPosition> toReturn = new ArrayList<>();
        for(int i = 0; i< Settings.BOARD_DIM; i++){
            for(int j = 0; j< Settings.BOARD_DIM; j++){
                toReturn.add(new ColorPosition(grid[i][j].toString(),grid[i][j].getImagePath(),i,j));
            }
        }
        return toReturn;
    }
}



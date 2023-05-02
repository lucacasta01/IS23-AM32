package it.polimi.myShelfie.utilities;

import it.polimi.myShelfie.model.Position;
import it.polimi.myShelfie.model.Tile;

public class ColorPosition {
    private String color;
    private String imgpath;
    private int row;
    private int column;

    public ColorPosition(String color, String imgpath, int row, int column) {
        this.color = color;
        this.imgpath = imgpath;
        this.row = row;
        this.column = column;
    }

    public String getImgpath() {
        return imgpath;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setImgpath(String imgpath) {
        this.imgpath = imgpath;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public String getColor() {
        return color;
    }

    /**
     *
     * @return <Tile.Color> tileColor
     */
    public Tile.Color getTileColor(){
        switch(color){
            case "BLUE" -> {return Tile.Color.BLUE;}
            case "LIGHTBLUE" -> {return Tile.Color.LIGHTBLUE;}
            case "WHITE" -> {return Tile.Color.WHITE;}
            case "PINK" -> {return Tile.Color.PINK;}
            case "YELLOW" -> {return Tile.Color.YELLOW;}
            case "GREEN" -> {return Tile.Color.GREEN;}
        }
        return Tile.Color.NULLTILE;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }
}

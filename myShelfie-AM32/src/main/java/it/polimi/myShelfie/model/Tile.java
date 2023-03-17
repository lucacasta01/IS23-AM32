package it.polimi.myShelfie.model;
public class Tile {

    enum Color {
        BLUE,
        LIGHTBLUE,
        WHITE,
        YELLOW,
        GREEN,
        PINK,
        NULLTILE
    }

    private String imagePath;
    private Color myColor;

    public Tile() {
        this.imagePath = imagePath;
        this.myColor = color;
    }

    /**
     * Returns the color of the related object
     *
     * @return Object's color
     */
    public Color getColor() {
        return this.myColor;
    }

    public static void main(String[] args) {
        Tile testTile = new Tile();
        System.out.println("Color: " + testTile.getColor());
    }
}

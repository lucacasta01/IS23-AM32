package it.polimi.myShelfie.model;
public class Tile {



    public enum Color {
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

    public Tile(String imagePath, Color color) {
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

    /**
     * Set the color of the related object
     *
     * @param color Object's color
     */
    public void setColor(Color color){
        this.myColor = color;
    }
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getImagePath() {
        return imagePath;
    }

    @Override
    public String toString(){
        switch(myColor){
            case BLUE -> {
                return "B";
            }
            case WHITE -> {
                return "W";
            }
            case LIGHTBLUE -> {
                return "L";
            }
            case PINK -> {
                return "P";
            }
            case GREEN -> {
                return "G";
            }
            case YELLOW -> {
                return "Y";
            }
            case NULLTILE -> {
                return "-";
            }
        }
        return "!";
    }
}

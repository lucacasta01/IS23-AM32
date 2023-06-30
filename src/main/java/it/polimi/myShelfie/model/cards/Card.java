package it.polimi.myShelfie.model.cards;


public abstract class Card {
    protected final String imgPath;

    public Card(String imgPath) {
        this.imgPath = imgPath;
    }

    /**
     * Returns the image path of the related object
     *
     * @return Object's image path
     */
    public String getImgPath() {
        return imgPath;
    }
}

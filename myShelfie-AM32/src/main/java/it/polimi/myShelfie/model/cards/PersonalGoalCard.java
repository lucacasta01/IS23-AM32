package it.polimi.myShelfie.model.cards;

import it.polimi.myShelfie.model.Tile;

public class PersonalGoalCard extends Card{
    private final Tile[][] patternToMatch;

    public PersonalGoalCard(String imgPath) {
        super(imgPath);
        this.patternToMatch = new Tile[6][5];
        for(int i=0;i<6;i++){
            for(int j=0;j<5;j++){
                patternToMatch[i][j] = new Tile("",Tile.Color.NULLTILE);
            }
        }
    }

    /**
     * Adds a constraint in the selected pattern's tile
     *
     * @param color Tile color
     * @param row Tile row
     * @param col Tile column
     */
    private void addConstraint(Tile.Color color, int row, int col){
        patternToMatch[row][col].setColor(color);
    }

    /**
     * Returns the card pattern
     *
     * @return Card pattern
     */
    public Tile[][] getPattern(){return patternToMatch;}

}

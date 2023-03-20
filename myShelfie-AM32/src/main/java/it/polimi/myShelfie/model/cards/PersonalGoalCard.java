package it.polimi.myShelfie.model.cards;

import it.polimi.myShelfie.model.Position;
import it.polimi.myShelfie.model.Tile;

import java.util.ArrayList;
import java.util.List;

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
     * @param pos Tile position, row and column
     *
     */
    private void addConstraint(Tile.Color color, Position pos){
        patternToMatch[pos.getRow()][pos.getColumn()].setColor(color);
    }

    /**
     * Returns the card pattern
     *
     * @return Card pattern
     */
    public Tile[][] getPattern(){return patternToMatch;}

    /**
     * set the entire constraints pattern
     * @param positions list of the constraints positions in the shelf
     * @param colors list of the tile colors related to the position (color colors[i] in position positions[i])
     */
    public void setPattern(ArrayList<Position> positions, ArrayList<Tile.Color> colors){
                for(int i=0; i<positions.size(); i++){
                    this.addConstraint(colors.get(i),positions.get(i));
                }
    }

}

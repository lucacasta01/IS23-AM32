package it.polimi.myShelfie.model.cards;

import it.polimi.myShelfie.model.Player;
import it.polimi.myShelfie.model.Position;
import it.polimi.myShelfie.model.Tile;
import it.polimi.myShelfie.utilities.Constants;

import java.util.ArrayList;
import java.util.List;

public class PersonalGoalCard extends Card{
    private final Tile[][] patternToMatch;


    public PersonalGoalCard(String imgPath) {
        super(imgPath);
        this.patternToMatch = new Tile[Constants.SHELFROW][Constants.SHELFCOLUMN];
        for(int i=0;i<Constants.SHELFROW;i++){
            for(int j=0;j<Constants.SHELFCOLUMN;j++){
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
     * set the entire constraints pattern, the color is linked to a specific position by his index
     * @param positions list of the constraints positions in the shelf
     * @param colors list of the tile colors related to the position (color colors[i] in position positions[i])
     */
    public void setPattern(List<Position> positions, List<Tile.Color> colors){
        for(int i=0; i<positions.size(); i++){
            this.addConstraint(colors.get(i),positions.get(i));
        }
    }


    private boolean isNull(){
        for(int i=0;i<Constants.SHELFROW;i++){
            for(int j=0;j<Constants.SHELFCOLUMN;j++){
                if(patternToMatch[i][j].getColor() != Tile.Color.NULLTILE)
                    return false;
            }
        }
        return true;
    }
    @Override
    public String toString(){
        if(isNull()){return "";}

        StringBuilder s = new StringBuilder();
        for(int i=0;i<Constants.SHELFROW;i++){
            for(int j=0;j<Constants.SHELFCOLUMN;j++){
                s.append(patternToMatch[i][j].toString()).append("\t");
            }
            s.append("\n");
        }
        return s.toString();
    }
}

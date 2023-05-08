package it.polimi.myShelfie.model.cards;
import it.polimi.myShelfie.utilities.Position;
import it.polimi.myShelfie.model.Shelf;
import it.polimi.myShelfie.model.Tile;
import it.polimi.myShelfie.utilities.Constants;
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

    public Integer getIndex(){
        String[] strings = imgPath.split("/");
        String myString = strings[2];
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(myString.charAt(14));
        if(myString.charAt(15) != '.'){
            stringBuilder.append(myString.charAt(15));
        }
        int toReturn = -1;
        try{
            toReturn = Integer.parseInt(stringBuilder.toString());
        }
        catch (Exception e){
            System.out.println(e.toString());
        }

        return toReturn;
    }

    public Integer checkPersonalGoal(Shelf s){
        Tile[][] toCheck = s.getTileMartrix();
        int score = 0;
        for(int i = 0; i<Constants.SHELFROW; i++){
            for(int j = 0; j<Constants.SHELFCOLUMN; j++){
                if(patternToMatch[i][j].getColor() == toCheck[i][j].getColor() && patternToMatch[i][j].getColor() != Tile.Color.NULLTILE){
                    score ++;
                }
            }
        }
        switch (score){
            case 1: return 1;
            case 2: return 2;
            case 3: return 4;
            case 4: return 6;
            case 5: return 9;
            case 6: return 12;
            default: return 0;
        }
    }
}

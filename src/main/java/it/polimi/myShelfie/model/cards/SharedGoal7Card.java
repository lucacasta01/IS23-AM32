package it.polimi.myShelfie.model.cards;
import it.polimi.myShelfie.model.Player;
import it.polimi.myShelfie.model.Tile;
import it.polimi.myShelfie.utilities.Settings;
import java.util.*;
public class SharedGoal7Card extends SharedGoalCard implements CheckSharedGoal {

    public SharedGoal7Card(String imgPath, int playerNumber) {

        super(imgPath, playerNumber);
    }

    /**
     * Returns true if the player passed by parameter has achieved the shared goal
     * adds the player to the achievedBy list if needed
     * @return Check result
     */
    public boolean checkPattern(Player p){
        if(isAchieved(p)){
            return false;
        }
        int properRows = 0;
        Tile[][] toCheck = p.getMyShelf().getTileMartrix();
        List<Tile.Color> colorList = new ArrayList<>();
        for(int i = 0; i< Settings.SHELFROW; i++){
            for(int j = 0; j< Settings.SHELFCOLUMN; j++){
                if(!colorList.contains(toCheck[i][j].getColor()) && toCheck[i][j].getColor() != Tile.Color.NULLTILE && isRowFull(toCheck[i])){
                    colorList.add(toCheck[i][j].getColor());
                }
            }
            if(!colorList.isEmpty()){
                if(colorList.size() <= 3) {
                    properRows++;
                }
                colorList.clear();
            }
        }
        if(properRows >= 4){
            addPlayer(p);
            return true;
        }
        else{
            return false;
        }
    }

    private boolean isRowFull(Tile[] arrayToCheck){
        for(Tile t : arrayToCheck){
            if(t.getColor() == Tile.Color.NULLTILE){
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return "Card 7: You must have 4 rows that have a maximum" +
                "of three different colors in them ";
    }
}
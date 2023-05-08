package it.polimi.myShelfie.model.cards;
import it.polimi.myShelfie.model.Player;
import it.polimi.myShelfie.model.Tile;
import it.polimi.myShelfie.utilities.Constants;
import java.util.*;
public class SharedGoal9Card extends SharedGoalCard implements CheckSharedGoal {

    public SharedGoal9Card(String imgPath, int playerNumber) {
        super(imgPath, playerNumber);
    }

    /**
     * Returns true if the player passed by parameter has achieved the shared goal
     *
     * @return Check result
     */
    public boolean checkPattern(Player p){
        if(isAchieved(p)){
            return false;
        }
        Map<Tile.Color, Integer> colorMap = new HashMap<>();
        colorMap.put(Tile.Color.BLUE, 0);
        colorMap.put(Tile.Color.LIGHTBLUE, 0);
        colorMap.put(Tile.Color.WHITE, 0);
        colorMap.put(Tile.Color.YELLOW, 0);
        colorMap.put(Tile.Color.GREEN, 0);
        colorMap.put(Tile.Color.PINK, 0);
        Tile[][] toCheck = p.getMyShelf().getTileMartrix();
        for(int i = 0; i< Constants.SHELFROW; i++){
            for(int j = 0; j<Constants.SHELFCOLUMN; j++){
                Tile.Color foundColor = toCheck[i][j].getColor();
                if(colorMap.containsKey(foundColor)){
                    colorMap.compute(foundColor, (k,v) -> v+1);
                }
            }
        }
        Integer max = 0;
        for(Map.Entry<Tile.Color, Integer> entry : colorMap.entrySet()){
            if(entry.getValue() > max){
                max = entry.getValue();
            }
        }
        if(max > 7){
            addPlayer(p);
            return true;
        }
        else return false;
    }

    @Override
    public String toString() {
        return "Card 9";
    }
}
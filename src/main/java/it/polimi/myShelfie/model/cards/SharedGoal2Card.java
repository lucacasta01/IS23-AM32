package it.polimi.myShelfie.model.cards;

import it.polimi.myShelfie.model.Player;
import it.polimi.myShelfie.model.Tile;
import it.polimi.myShelfie.utilities.Constants;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class SharedGoal2Card extends SharedGoalCard implements CheckSharedGoal {

    private Map<Tile.Color,Boolean> colorBooleanMap;

    public SharedGoal2Card(String imgPath, int playerNumber) {
        super(imgPath, playerNumber);
        colorBooleanMap = new HashMap<>();
        colorBooleanMap.put(Tile.Color.BLUE, false);
        colorBooleanMap.put(Tile.Color.LIGHTBLUE, false);
        colorBooleanMap.put(Tile.Color.WHITE, false);
        colorBooleanMap.put(Tile.Color.YELLOW, false);
        colorBooleanMap.put(Tile.Color.GREEN, false);
        colorBooleanMap.put(Tile.Color.PINK, false);
    }

    private void initMap(){
        colorBooleanMap.forEach((key,value) -> colorBooleanMap.replace(key,false));
    }

    /**
     * Returns true if the player passed by parameter has achieved the shared goal
     *
     * @return Check result
     */
    @Override
    public boolean checkPattern(Player p) {
        Tile[][] matrix = p.getMyShelf().getTileMartrix();

        int count = 0;
        AtomicBoolean flag = new AtomicBoolean(true);

        for (int col = 0; col < Constants.SHELFCOLUMN; col++) {
            initMap();
            for (int row = 0; row < Constants.SHELFROW; row++) {
                if(matrix[row][col].getColor() == Tile.Color.NULLTILE){
                    row = Constants.SHELFROW;
                    continue;
                }
                if (!colorBooleanMap.get(matrix[row][col].getColor())){
                    colorBooleanMap.replace(matrix[row][col].getColor(),true);
                }
                else{
                    row = Constants.SHELFROW;
                }
            }
            colorBooleanMap.forEach((key,value)->{
                if(!colorBooleanMap.get(key)){
                    flag.set(false);
                }
            });

            if(flag.get()){
                count++;
            }
            else{
                flag.set(true);
            }
        }

        if(count>=2){
            addPlayer(p);
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    public String toString() {
        return "Card 2";
    }
}
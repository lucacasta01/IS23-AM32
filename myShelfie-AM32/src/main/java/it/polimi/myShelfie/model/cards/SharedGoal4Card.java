package it.polimi.myShelfie.model.cards;

import it.polimi.myShelfie.model.Player;
import it.polimi.myShelfie.model.Tile;
import it.polimi.myShelfie.utilities.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class SharedGoal4Card extends SharedGoalCard implements CheckSharedGoal {


    public SharedGoal4Card(String imgPath) {
        super(imgPath);
    }
    /**
     * Returns true if the player passed by parameter has achieved the shared goal
     *
     * @return Check result
     */
    public boolean checkPattern(Player p){

        int numberOfPairs = 0;
        Tile[][] grid = p.getMyShelf().getTileMartrix();
        if(!isAchieved(p)){
            for(int col = 0; col< Constants.SHELFCOLUMN; col++){
                for(int row = 0; row<Constants.SHELFROW-1; row++){
                    if(grid[row][col].getColor()==grid[row+1][col].getColor()){
                        row++;
                        numberOfPairs++;
                    }
                }
            }
            if(numberOfPairs>=6){
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }

    }
}
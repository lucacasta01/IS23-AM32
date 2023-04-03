package it.polimi.myShelfie.model.cards;

import it.polimi.myShelfie.model.Player;
import it.polimi.myShelfie.model.Shelf;
import it.polimi.myShelfie.model.Tile;
import it.polimi.myShelfie.utilities.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class SharedGoal12Card extends SharedGoalCard implements CheckSharedGoal {

    public SharedGoal12Card(String imgPath) {
        super(imgPath);
    }

    /**
     * Returns true if the player passed by parameter has achieved the shared goal
     *
     * @return Check result
     */
    public boolean checkPattern(Player p){
        if(isAchieved(p)){return false;}

        Shelf playerShelf = p.getMyShelf();
        Tile[][] matrix = playerShelf.getTileMartrix();
        int columnCount[] = new int[]{0,0,0,0,0};

        for(int i = 0; i< Constants.SHELFROW - 1; i++) {
            for (int j = 0; j < Constants.SHELFCOLUMN - 1; j++) {
                if(!matrix[i][j].getColor().equals(Tile.Color.NULLTILE));
                columnCount[j] ++;
            }
        }
        int i=0;
        if(columnCount[i] > columnCount[i+1]) {
            for(int j = 0; j< Constants.SHELFROW - 2; j++){
                if (columnCount[j] != columnCount[j + 1] + 1) {return false;}
            }
            return true;
        }
        else if(columnCount[i] < columnCount[i+1]){
            for(int j = 0; j< Constants.SHELFROW - 2; i++) {
                if (columnCount[i] != columnCount[i + 1] - 1) {return false;}
            }
            return true;
        }
        else if(columnCount[i] == columnCount[i+1]){return false;}

        return false;
    }
}
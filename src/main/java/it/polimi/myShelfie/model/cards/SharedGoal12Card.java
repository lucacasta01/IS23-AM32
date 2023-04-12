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
        int columnCount[] = {0, 0, 0, 0, 0};
        final int minHigh = 5;

        for(int i = 0; i< Constants.SHELFROW; i++) {
            for (int j = 0; j < Constants.SHELFCOLUMN; j++) {
                if(!(matrix[i][j].getColor().equals(Tile.Color.NULLTILE))){
                columnCount[j]++;
                }
            }
        }

        for(int i = 0; i< Constants.SHELFCOLUMN; i++){
            if(columnCount[i] == 0){return false;}
        }

        int i=0;
        if(columnCount[i] > columnCount[i+1]) {
            if(columnCount[i] < minHigh){return false;}
                for(int j = 0; j < Constants.SHELFCOLUMN - 1; j++){
                    if (columnCount[j] != (columnCount[j + 1] + 1)) {return false;}
                }
            return true;
        }
        else if(columnCount[i] < columnCount[i+1]){
            if(columnCount[Constants.SHELFCOLUMN - 1] < minHigh){return false;}
                for(int j = 0; j< Constants.SHELFCOLUMN - 1; j++) {
                    if (columnCount[j] != columnCount[j + 1] - 1) {return false;}
                }
            return true;
        }
        else if(columnCount[i] == columnCount[i+1]){return false;}

        return false;
    }
}
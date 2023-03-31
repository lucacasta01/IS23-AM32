package it.polimi.myShelfie.model.cards;

import it.polimi.myShelfie.model.Player;
import it.polimi.myShelfie.model.Shelf;
import it.polimi.myShelfie.model.Tile;
import it.polimi.myShelfie.utilities.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class SharedGoal11Card extends SharedGoalCard implements CheckSharedGoal {

    public SharedGoal11Card(String imgPath) {
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
        int countEquals = 0;
        final int controlli = 4;

        for(int i = 0; i < Constants.SHELFCOLUMN - 2; i++){
            if(matrix[i][i].getColor().equals(matrix[i+1][i+1].getColor())){
            countEquals++;
            }
        }
        if(countEquals == controlli){return true;}

        for(int i = 0; i < Constants.SHELFCOLUMN - 2; i++) {
            if (i == 0) {
                countEquals = 0;
            }
            if (matrix[i + 1][i].getColor().equals(matrix[i + 2][i + 1].getColor())){

            countEquals++;
            }
        }
        if(countEquals == controlli){return true;}

        int j=0;
        for(int i = Constants.SHELFCOLUMN - 1; i > 0; i--){
            if (i == Constants.SHELFCOLUMN - 1) {
                countEquals = 0;
            }
            if (matrix[i][j].getColor().equals(matrix[i-1][j+1].getColor())){
            countEquals++;
            }
            j++;
        }if(countEquals == controlli){return true;}

        int k=0;
        for(int i = Constants.SHELFCOLUMN ; i > 1; i--){
            if (i == Constants.SHELFCOLUMN) {
                countEquals = 0;
            }
            if (matrix[i][k].getColor().equals(matrix[i-1][k+1].getColor())){
                countEquals++;
            }
            k++;
        }if(countEquals == controlli){return true;}

        return false;
    }
}
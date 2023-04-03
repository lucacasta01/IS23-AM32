package it.polimi.myShelfie.model.cards;

import it.polimi.myShelfie.model.Player;
import it.polimi.myShelfie.model.Shelf;
import it.polimi.myShelfie.model.Tile;
import it.polimi.myShelfie.utilities.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class SharedGoal8Card extends SharedGoalCard implements CheckSharedGoal {


    public SharedGoal8Card(String imgPath) {
        super(imgPath);
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
        Tile[][] toCheck = p.getMyShelf().getTileMartrix();
        if(toCheck[0][0].getColor() == toCheck[Constants.SHELFROW-1][0].getColor()
                && toCheck[Constants.SHELFROW-1][Constants.SHELFCOLUMN-1].getColor() == toCheck[Constants.SHELFROW-1][0].getColor()
                && toCheck[Constants.SHELFROW-1][0].getColor() == toCheck[0][Constants.SHELFCOLUMN-1].getColor()){
            addPlayer(p);
            return true;
        }
        else return false;
    }
}
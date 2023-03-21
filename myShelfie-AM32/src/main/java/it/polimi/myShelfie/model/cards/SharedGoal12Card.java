package it.polimi.myShelfie.model.cards;

import it.polimi.myShelfie.model.Player;

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
        //does some stuff
        return true;
    }
}
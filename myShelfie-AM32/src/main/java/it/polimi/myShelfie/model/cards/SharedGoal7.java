package it.polimi.myShelfie.model.cards;

import it.polimi.myShelfie.model.Player;

public class SharedGoal7 extends SharedGoalCard implements CheckSharedGoal {
    SharedGoal7(){super("");}

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
package it.polimi.myShelfie.model.cards;

import it.polimi.myShelfie.model.Player;

public class SharedGoal1 extends SharedGoalCard implements CheckSharedGoal {
    SharedGoal1(){super("");}

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
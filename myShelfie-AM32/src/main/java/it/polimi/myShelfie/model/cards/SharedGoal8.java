package it.polimi.myShelfie.model.cards;

public class SharedGoal8 extends SharedGoalCard implements CheckSharedGoal {
    SharedGoal8(){super("");}

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
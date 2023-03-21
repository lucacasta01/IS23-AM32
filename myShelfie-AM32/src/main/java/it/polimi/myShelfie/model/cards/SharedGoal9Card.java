package it.polimi.myShelfie.model.cards;

import it.polimi.myShelfie.model.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class SharedGoal9Card extends Card implements SharedGoalCard {
    private Stack<Integer> pointsTokenStack;
    private List<Player> achievedBy;

    public SharedGoal9Card(String imgPath) {

        super(imgPath);
        this.pointsTokenStack = new Stack<>();
        pointsTokenStack.add(4);
        pointsTokenStack.add(6);
        pointsTokenStack.add(8);

        this.achievedBy = new ArrayList<Player>();

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

    /**
     * Pop one point-token from the card stack.
     *
     * @return Value of point-token (if present), 0 otherwise
     */
    public Integer popPointToken(){
        int p=0;
        try{
            p = pointsTokenStack.pop();
        }
        catch (Exception e){
            return p;
        }
        return p;
    }

    /**
     * Check if Player passed by param has achieved the goal
     *
     * @param p Player to check
     * @return true if p has achieved the goal
     */
    public boolean isAchieved(Player p){
        return achievedBy.contains(p);
    }


    /**
     * adds the player p to the achievedBy list
     * @param p Player to add
     */
    public void addPlayer(Player p){
        achievedBy.add(p);
    }
}
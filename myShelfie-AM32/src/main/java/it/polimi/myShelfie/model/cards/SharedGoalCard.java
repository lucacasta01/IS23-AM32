package it.polimi.myShelfie.model.cards;

import java.util.List;
import java.util.Stack;

public class SharedGoalCard extends Card{
    private Stack<Integer> pointsTokenStack;
    private List<Player> achievedBy;

    public SharedGoalCard(String imgPath) {
        super(imgPath);

        this.pointsTokenStack = new Stack<>();
        pointsTokenStack.add(4);
        pointsTokenStack.add(6);
        pointsTokenStack.add(8);

        this.achievedBy = new List<Player>();

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
}


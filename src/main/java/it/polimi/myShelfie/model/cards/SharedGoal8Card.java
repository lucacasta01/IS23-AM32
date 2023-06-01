package it.polimi.myShelfie.model.cards;
import it.polimi.myShelfie.model.Player;
import it.polimi.myShelfie.model.Tile;
import it.polimi.myShelfie.utilities.Settings;
public class SharedGoal8Card extends SharedGoalCard implements CheckSharedGoal {


    public SharedGoal8Card(String imgPath, int playerNumber) {
        super(imgPath, playerNumber);
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
        if((toCheck[0][0].getColor() == toCheck[Settings.SHELFROW-1][0].getColor()
                && toCheck[Settings.SHELFROW-1][Settings.SHELFCOLUMN-1].getColor() == toCheck[Settings.SHELFROW-1][0].getColor()
                && toCheck[Settings.SHELFROW-1][0].getColor() == toCheck[0][Settings.SHELFCOLUMN-1].getColor())&&toCheck[0][0].getColor()!= Tile.Color.NULLTILE){
            addPlayer(p);
            return true;
        }
        else return false;
    }

    @Override
    public String toString() {
        return "Card 8: your top-left, top-right, bottom-left and bottom-right tiles" +
                "must have the same color";
    }
}
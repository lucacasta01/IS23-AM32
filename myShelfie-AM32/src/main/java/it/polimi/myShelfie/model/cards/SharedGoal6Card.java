package it.polimi.myShelfie.model.cards;

import it.polimi.myShelfie.model.Player;
import it.polimi.myShelfie.model.Tile;
import it.polimi.myShelfie.utilities.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class SharedGoal6Card extends SharedGoalCard implements CheckSharedGoal {
    private Stack<Integer> pointsTokenStack;
    private List<Player> achievedBy;

    public SharedGoal6Card(String imgPath) {

        super(imgPath);
    }

    /**
     * Returns true if the player passed by parameter has achieved the shared goal
     *
     * @return Check result
     */
    public boolean checkPattern(Player p){
        boolean  g = false, b=false, lb=false, w=false, y=false, pi=false;
        int achievedRows = 0;

        if(!isAchieved(p)){
            for(int row = 0; row< Constants.SHELFROW; row++){
                for(int col = 0; col < Constants.SHELFCOLUMN; col++){

                    if((p.getMyShelf().getTileMartrix()[row][col].getColor()== Tile.Color.BLUE)&&(p.getMyShelf().getTileMartrix()[row][col].getColor()!=Tile.Color.NULLTILE)){
                        b=true;
                    }else if((p.getMyShelf().getTileMartrix()[row][col].getColor()== Tile.Color.GREEN)&&(p.getMyShelf().getTileMartrix()[row][col].getColor()!=Tile.Color.NULLTILE)){
                        g=true;
                    }else if((p.getMyShelf().getTileMartrix()[row][col].getColor()== Tile.Color.LIGHTBLUE)&&(p.getMyShelf().getTileMartrix()[row][col].getColor()!=Tile.Color.NULLTILE)){
                        lb = true;
                    }else if((p.getMyShelf().getTileMartrix()[row][col].getColor()== Tile.Color.WHITE)&&(p.getMyShelf().getTileMartrix()[row][col].getColor()!=Tile.Color.NULLTILE)){
                        w=true;
                    }else if((p.getMyShelf().getTileMartrix()[row][col].getColor()== Tile.Color.YELLOW)&&(p.getMyShelf().getTileMartrix()[row][col].getColor()!=Tile.Color.NULLTILE)){
                        y=true;
                    }else if((p.getMyShelf().getTileMartrix()[row][col].getColor()== Tile.Color.PINK)&&(p.getMyShelf().getTileMartrix()[row][col].getColor()!=Tile.Color.NULLTILE)){
                        pi=true;
                    }
                }
                if(b && g && lb && w && y && pi){
                    achievedRows++;
                }
                b=false; g=false; lb=false; w=false; y=false; pi=false;
            }
            if(achievedRows>=3){
                addPlayer(p);
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }
}
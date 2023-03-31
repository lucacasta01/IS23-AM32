package it.polimi.myShelfie.model.cards;

import it.polimi.myShelfie.model.Player;
import it.polimi.myShelfie.model.Tile;
import it.polimi.myShelfie.utilities.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class SharedGoal5Card extends SharedGoalCard implements CheckSharedGoal {


    public SharedGoal5Card(String imgPath) {
        super(imgPath);
    }

    /**
     * Returns true if the player passed by parameter has achieved the shared goal
     *
     * @return Check result
     */
    public boolean checkPattern(Player p){
       if(!isAchieved(p)){
           int achievedCols = 0;
           int numberOfColors = 0;
           boolean  g = false, b=false, lb=false, w=false, y=false, pi=false;
            for(int col = 0; col< Constants.SHELFCOLUMN; col++){
                for(int row=0; row<Constants.SHELFROW; row++){
                    switch (p.getMyShelf().getTileMartrix()[row][col].getColor()){
                        case BLUE:
                            b=true;
                            break;
                        case GREEN:
                            g=true;
                            break;
                        case LIGHTBLUE:
                            lb=true;
                            break;
                        case YELLOW:
                            y=true;
                            break;
                        case PINK:
                            pi=true;
                            break;
                        case WHITE:
                            w=true;
                            break;
                    }
                }
                if(b=true){
                    numberOfColors++;
                }
                if(g=true){
                    numberOfColors++;
                }
                if(lb=true){
                    numberOfColors++;
                }
                if(y=true){
                    numberOfColors++;
                }
                if(pi=true){
                    numberOfColors++;
                }
                if(w=true){
                    numberOfColors++;
                }
                if(numberOfColors<=3){
                    achievedCols++;
                }
                b=false; g=false; lb=false; y=false; pi=false; w=false;


            }
            if(achievedCols>=3){
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
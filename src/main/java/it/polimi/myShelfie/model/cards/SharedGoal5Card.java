package it.polimi.myShelfie.model.cards;
import it.polimi.myShelfie.model.Player;
import it.polimi.myShelfie.utilities.Settings;
public class SharedGoal5Card extends SharedGoalCard implements CheckSharedGoal {


    public SharedGoal5Card(String imgPath, int playerNumber) {
        super(imgPath, playerNumber);
    }

    /**
     * Returns true if the player passed by parameter has achieved the shared goal
     * adds the player to the achievedBy list if needed
     * @return Check result
     */
    public boolean checkPattern(Player p){
       if(!isAchieved(p)){
           int achievedCols = 0;
           int numberOfColors = 0;
           int numberOfNulls = 0;
           boolean  g = false, b=false, lb=false, w=false, y=false, pi=false;
            for(int col = 0; col< Settings.SHELFCOLUMN; col++){
                for(int row = 0; row< Settings.SHELFROW; row++){
                    switch (p.getMyShelf().getTileMartrix()[row][col].getColor()) {
                        case BLUE -> b = true;
                        case GREEN -> g = true;
                        case LIGHTBLUE -> lb = true;
                        case YELLOW -> y = true;
                        case PINK -> pi = true;
                        case WHITE -> w = true;
                        case NULLTILE -> numberOfNulls++;
                        default -> {
                        }
                    }
                }
                if(b){
                    numberOfColors++;
                }
                if(g){
                    numberOfColors++;
                }
                if(lb){
                    numberOfColors++;
                }
                if(y){
                    numberOfColors++;
                }
                if(pi){
                    numberOfColors++;
                }
                if(w){
                    numberOfColors++;
                }

                if(numberOfColors<=3&&numberOfNulls!=6){
                    achievedCols++;
                }
                b=false; g=false; lb=false; y=false; pi=false; w=false;
                numberOfColors = 0;
                numberOfNulls = 0;
            }

            if(achievedCols>=3){
                super.addPlayer(p);
                return true;
            }else{
                return false;
            }
       }else{
           return false;
       }
    }

    @Override
    public String toString() {
        return "Card 5: You must have 3 columns that have a maximum of three different colors" +
                "in each one of them";
    }
}
package it.polimi.myShelfie.model.cards;

import it.polimi.myShelfie.model.Player;
import it.polimi.myShelfie.model.Tile;
import it.polimi.myShelfie.utilities.Settings;
public class SharedGoal4Card extends SharedGoalCard implements CheckSharedGoal {


    public SharedGoal4Card(String imgPath, int playerNumber) {
        super(imgPath,playerNumber);
    }
    /**
     * Returns true if the player passed by parameter has achieved the shared goal
     * adds the player to the achievedBy list if needed
     * @return Check result
     */
    public boolean checkPattern(Player p){

        int numberOfColPairs = 0;
        int numberOfRowPairs = 0;
        Tile[][] grid = p.getMyShelf().getTileMartrix();
        if(!isAchieved(p)){
            for(int col = 0; col< Settings.SHELFCOLUMN; col++){
                for(int row = 0; row< Settings.SHELFROW-1; row++){
                    if((grid[row][col].getColor()==grid[row+1][col].getColor())&&(grid[row][col].getColor()!=Tile.Color.NULLTILE)){
                        row++;
                        numberOfColPairs++;
                    }
                }
            }
            for(int row = 0; row < Settings.SHELFROW; row++){
                for(int col = 0; col< Settings.SHELFCOLUMN-1; col++){
                    if((grid[row][col].getColor()==grid[row][col+1].getColor())&&(grid[row][col].getColor()!=Tile.Color.NULLTILE)){
                        col++;
                        numberOfRowPairs++;
                    }
                }
            }
            if(numberOfColPairs>=6 || numberOfRowPairs>=6){
                addPlayer(p);
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
        return "Card 4: You must have six 2x1 group of tiles that have the same color";
    }
}
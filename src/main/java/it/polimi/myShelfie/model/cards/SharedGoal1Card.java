package it.polimi.myShelfie.model.cards;
import it.polimi.myShelfie.model.Player;
import it.polimi.myShelfie.model.Shelf;
import it.polimi.myShelfie.model.Tile;
import it.polimi.myShelfie.utilities.Settings;
public class SharedGoal1Card extends SharedGoalCard implements CheckSharedGoal {
    public SharedGoal1Card(String imgPath, int playerNumber) {
        super(imgPath, playerNumber);
    }

    /*
    * Goal: Two equals 2x2 squares
    */

    /**
     * Returns true if the player passed by parameter has achieved the shared goal
     * adds the player to the achievedBy list if needed
     * @return Check result
     */
    @Override
    public boolean checkPattern(Player p){
        if(isAchieved(p)){
            return false;
        }
        Shelf playerShelf = p.getMyShelf();
        Tile[][] matrix = playerShelf.getTileMartrix();
        int count = 0;

        for(int i = 0; i< Settings.SHELFROW-1; i++){
            for(int j = 0; j< Settings.SHELFCOLUMN-1; j++){
                Tile[][] submatrix1 = {
                        {matrix[i][j],matrix[i][j+1]},
                        {matrix[i+1][j],matrix[i+1][j+1]}
                };
                for(int k = i; k < Settings.SHELFROW - 1; k++){
                    for(int l = 0; l < Settings.SHELFCOLUMN - 1; l++){
                        if(k == i && l <= j){
                            continue;
                        }
                        if(k==i+1 || l==j+1){
                            continue;
                        }
                        Tile[][] submatrix2 = {
                                {matrix[k][l], matrix[k][l+1]},
                                {matrix[k+1][l], matrix[k+1][l+1]}
                        };
                        if(equalsSubmatrixs(submatrix1, submatrix2)){
                            count++;
                        }
                    }
                }
            }
        }

        if(count>=1){
            addPlayer(p);
            return true;
        }
        else{
            return false;
        }
    }

    private boolean equalsSubmatrixs(Tile[][] sub1, Tile[][] sub2){
        for(int i=0; i<sub1.length; i++){
            for(int j=0;j<sub1[0].length;j++){
                if(sub1[i][j].getColor() == Tile.Color.NULLTILE || sub2[i][j].getColor() == Tile.Color.NULLTILE){
                    return false;
                }
                if(sub1[i][j].getColor() != sub2[i][j].getColor())
                    return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return "Card 1: Create two 2x2 groups of tiles that have the same color";
    }
}
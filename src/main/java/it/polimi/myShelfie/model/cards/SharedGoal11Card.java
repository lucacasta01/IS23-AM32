package it.polimi.myShelfie.model.cards;
import it.polimi.myShelfie.model.Player;
import it.polimi.myShelfie.model.Tile;
import it.polimi.myShelfie.utilities.Constants;
public class SharedGoal11Card extends SharedGoalCard implements CheckSharedGoal {

    public SharedGoal11Card(String imgPath, int playerNumber) {
        super(imgPath, playerNumber);
    }


    /**
     * Returns true if the player passed by parameter has achieved the shared goal
     * adds the player to the achievedBy list if needed
     * @return Check result
     */
    public boolean checkPattern(Player p){
        if(isAchieved(p)){return false;}
        Tile[][] matrix = p.getMyShelf().getTileMartrix();
        int countEquals = 0;
        final int threshold = 4;

        for(int i = 0; i < Constants.SHELFROW - 2; i++){
            if(matrix[i][i].getColor().equals(matrix[i+1][i+1].getColor()) && !(matrix[i][i].getColor().equals(Tile.Color.NULLTILE))){
                countEquals++;
            }
        }
        if(countEquals == threshold){
            addPlayer(p);
            return true;
        }


        countEquals=0;
        for(int i = 0; i < Constants.SHELFROW - 2; i++) {
            if (matrix[i+1][i].getColor().equals(matrix[i + 2][i + 1].getColor()) && !(matrix[i+1][i].getColor().equals(Tile.Color.NULLTILE))){
                countEquals++;
            }
        }
        if(countEquals == threshold){
            addPlayer(p);
            return true;
        }

        int j=0;
        countEquals=0;
        for(int i = Constants.SHELFROW - 2; i > 0; i--){
            if (matrix[i][j].getColor().equals(matrix[i-1][j+1].getColor()) && !(matrix[i][j].getColor().equals(Tile.Color.NULLTILE))){
            countEquals++;
            }
            j++;
        }if(countEquals == threshold){
            addPlayer(p);
            return true;
        }

        int k=0;
        countEquals=0;
        for(int i = Constants.SHELFROW - 1 ; i > 1; i--){
            if (matrix[i][k].getColor().equals(matrix[i-1][k+1].getColor()) && !(matrix[i][k].getColor().equals(Tile.Color.NULLTILE))){
                countEquals++;
            }
            k++;
        }if(countEquals == threshold){
            addPlayer(p);
            return true;
        }

        return false;
    }

    @Override
    public String toString() {
        return "Card 11: You must have a diagonal made up with tiles" +
                "of the same color";
    }
}
package it.polimi.myShelfie.model.cards;
import it.polimi.myShelfie.model.Player;
import it.polimi.myShelfie.model.Shelf;
import it.polimi.myShelfie.model.Tile;
import it.polimi.myShelfie.utilities.Settings;
public class SharedGoal10Card extends SharedGoalCard implements CheckSharedGoal {
    public SharedGoal10Card(String imgPath, int playerNumber) {
        super(imgPath, playerNumber);
    }

    /**
     * Returns true if the player passed by parameter has achieved the shared goal
     * adds the player to the achievedBy list if needed
     * @return Check result
     */
    public boolean checkPattern(Player p){

        if(isAchieved(p)){
            return false;
        }

        Shelf playerShelf = p.getMyShelf();
        Tile[][] matrix = playerShelf.getTileMartrix();

        for(int i = 0; i < Settings.SHELFROW - 2; i++){
            for(int j = 0; j < Settings.SHELFCOLUMN - 2; j++){
                if((matrix[i][j].getColor().equals(matrix[i][j+2].getColor())) && !(matrix[i][j].getColor().equals(Tile.Color.NULLTILE)) &&
                        (matrix[i][j].getColor().equals(matrix[i+1][j+1].getColor())) &&
                        (matrix[i][j].getColor().equals(matrix[i+2][j].getColor())) &&
                        (matrix[i][j].getColor().equals(matrix[i+2][j+2].getColor()))){
                    addPlayer(p);
                    return true;
                }
            }
        }
        return false;
    }
    @Override
    public String toString() {
        return "Card 10: You must have an X shaped formation made with tiles of the same color";
    }
}
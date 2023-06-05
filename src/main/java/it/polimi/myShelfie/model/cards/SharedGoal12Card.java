package it.polimi.myShelfie.model.cards;
import it.polimi.myShelfie.model.Player;
import it.polimi.myShelfie.model.Shelf;
import it.polimi.myShelfie.model.Tile;
import it.polimi.myShelfie.utilities.Settings;
public class SharedGoal12Card extends SharedGoalCard implements CheckSharedGoal {

    public SharedGoal12Card(String imgPath, int playerNumber) {
        super(imgPath, playerNumber);
    }

    /**
     * Returns true if the player passed by parameter has achieved the shared goal
     * adds the player to the achievedBy list if needed
     * @return Check result
     */
    public boolean checkPattern(Player p){
        if(isAchieved(p)){return false;}

        Shelf playerShelf = p.getMyShelf();
        Tile[][] matrix = playerShelf.getTileMartrix();
        int[] columnCount = {0, 0, 0, 0, 0};
        final int minHigh = 5;

        for(int i = 0; i< Settings.SHELFROW; i++) {
            for (int j = 0; j < Settings.SHELFCOLUMN; j++) {
                if(!(matrix[i][j].getColor().equals(Tile.Color.NULLTILE))){
                columnCount[j]++;
                }
            }
        }

        for(int i = 0; i< Settings.SHELFCOLUMN; i++){
            if(columnCount[i] == 0){return false;}
        }

        int i=0;
        if(columnCount[i] > columnCount[i+1]) {
            if(columnCount[i] < minHigh){return false;}
                for(int j = 0; j < Settings.SHELFCOLUMN - 1; j++){
                    if (columnCount[j] != (columnCount[j + 1] + 1)) {return false;}
                }
            addPlayer(p);
            return true;
        }
        else if(columnCount[i] < columnCount[i+1]){
            if(columnCount[Settings.SHELFCOLUMN - 1] < minHigh){return false;}
                for(int j = 0; j< Settings.SHELFCOLUMN - 1; j++) {
                    if (columnCount[j] != columnCount[j + 1] - 1) {return false;}
                }
            addPlayer(p);
            return true;

        }
        else if(columnCount[i] == columnCount[i+1]){return false;}

        return false;
    }

    @Override
    public String toString() {
        return "Card 12: fill your shelf up to the diagonal";
    }
}
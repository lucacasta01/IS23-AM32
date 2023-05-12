package it.polimi.myShelfie.model.cards;
import it.polimi.myShelfie.model.Player;
import it.polimi.myShelfie.model.Tile;
import it.polimi.myShelfie.utilities.Settings;
public class SharedGoal3Card extends SharedGoalCard implements CheckSharedGoal {

    public boolean[][] flags;
    private int count_tiles=0;

    public SharedGoal3Card(String imgPath, int playerNumber) {
        super(imgPath, playerNumber);
        flags = new boolean[Settings.SHELFROW][Settings.SHELFCOLUMN];
        initFlags();
    }

    public void initFlags(){
        for(int i = 0; i< Settings.SHELFROW; i++){
            for(int j = 0; j< Settings.SHELFCOLUMN; j++){
                flags[i][j] = false;
            }
        }
    }

    /**
    * Returns true if the player passed by parameter has achieved the shared goal
    * adds the player to the achievedBy list if needed
    * @return Check result
    */
    public boolean checkPattern(Player p) {
        if(isAchieved(p)){
            return false;
        }
        Tile[][] matrix = p.getMyShelf().getTileMartrix();
        initFlags();
        int tilesPerGroup = 4;
        int numOfGroup = 4;
        int count_groups = 0;

        for(int row = Settings.SHELFROW-1; row >= 0; row--){
            for(int column = 0; column < Settings.SHELFCOLUMN; column++){

                if(!matrix[row][column].getColor().equals(Tile.Color.NULLTILE) || !flags[row][column]){
                    flags[row][column]=true;
                    if(countElem(row, column,count_tiles, flags, matrix)==tilesPerGroup) count_groups++;
                    if(count_groups==numOfGroup){
                        addPlayer(p);
                        return true;
                    }

                }
                count_tiles = 0;
            }
        }
        return false;
    }

    public int countElem(int row, int column,int count_tiles, boolean[][] flags, Tile[][] matrix) {

        if(!matrix[row][column].getColor().equals(Tile.Color.NULLTILE) || !flags[row][column]) {


            flags[row][column] = true;

            //up
            if(row!=0){
                if (matrix[row][column].getColor().equals(matrix[row - 1][column].getColor()) && !flags[row-1][column]) {
                    count_tiles += countElem(row -1, column, count_tiles, flags, matrix);
                }
            }
            //down
            if(row!=5){
                if (matrix[row][column].getColor().equals(matrix[row + 1][column].getColor()) && !flags[row+1][column]) {
                    count_tiles += countElem(row +1, column, count_tiles, flags, matrix);
                }
            }
            //right
            if(column!=4) {
                if (matrix[row][column].getColor().equals(matrix[row][column + 1].getColor()) && !flags[row][column+1]) {
                    count_tiles += countElem(row, column + 1, count_tiles, flags, matrix);
                }
            }
            //left
            if(column!=0) {
                if (matrix[row][column].getColor().equals(matrix[row][column - 1].getColor()) && !flags[row][column-1]) {
                    count_tiles += countElem(row, column - 1, count_tiles, flags, matrix);
                }
            }
            return count_tiles+1;
        }
        return count_tiles;
    }

    @Override
    public String toString() {
        return "Card 3: You must have 4 groups of tiles that meet this requirement:\n" +
                "4 Tiles of the same color stacked on top of each other";
    }
}
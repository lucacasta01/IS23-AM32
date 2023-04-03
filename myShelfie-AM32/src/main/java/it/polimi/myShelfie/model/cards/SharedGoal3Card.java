package it.polimi.myShelfie.model.cards;

import it.polimi.myShelfie.model.Player;
import it.polimi.myShelfie.model.Tile;
import it.polimi.myShelfie.utilities.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class SharedGoal3Card extends SharedGoalCard implements CheckSharedGoal {

    private boolean[][] flags;

    public SharedGoal3Card(String imgPath) {
        super(imgPath);
        flags = new boolean[Constants.SHELFROW][Constants.SHELFCOLUMN];
        initFlags();
    }

    private void initFlags(){
        for(int i=0;i<Constants.SHELFROW;i++){
            for(int j=0;j<Constants.SHELFCOLUMN;j++){
                flags[i][j] = false;
            }
        }
    }

    /**
     * Returns true if the player passed by parameter has achieved the shared goal
     *
     * @return Check result
     */
    public boolean checkPattern(Player p){
        Tile[][] matrix = p.getMyShelf().getTileMartrix();
        initFlags();
        boolean trueFound = false;
        int count = 0;

        /*Vertical groups analysis
            3 vertical groups per column (mutual exclusion)
         */
        for(int col=0; col < Constants.SHELFCOLUMN; col++){
                for(int row = 0; row < Constants.SHELFROW - 3; row++){
                    if(matrix[row][col].getColor() == Tile.Color.NULLTILE
                    || matrix[row+1][col].getColor() == Tile.Color.NULLTILE
                    || matrix[row+2][col].getColor() == Tile.Color.NULLTILE
                    || matrix[row+3][col].getColor() == Tile.Color.NULLTILE){
                        continue;
                    }

                    if(matrix[row][col].getColor() == matrix[row+1][col].getColor()
                            && matrix[row+1][col].getColor() == matrix[row+2][col].getColor()
                            && matrix[row+2][col].getColor() == matrix[row+3][col].getColor()
                            && !flags[row][col]
                            && !flags[row+1][col]
                            && !flags[row+2][col]
                            && !flags[row+3][col]){
                        count++;
                        for(int i=0;i<4;i++){
                            flags[row+i][col] = true;
                        }
                        row = Constants.SHELFROW - 3;
                    }
                }
        }

        /*Horizontal groups analysis
            2 horizontal groups per column (mutual exclusion)
         */
        for(int row=0; row < Constants.SHELFROW; row++){
            for(int col = 0; col < Constants.SHELFCOLUMN - 3; col++){
                if(matrix[row][col].getColor() == Tile.Color.NULLTILE
                        || matrix[row][col+1].getColor() == Tile.Color.NULLTILE
                        || matrix[row][col+2].getColor() == Tile.Color.NULLTILE
                        || matrix[row][col+3].getColor() == Tile.Color.NULLTILE){
                    continue;
                }

                if(matrix[row][col].getColor() == matrix[row][col+1].getColor()
                        && matrix[row][col+1].getColor() == matrix[row][col+2].getColor()
                        && matrix[row][col+2].getColor() == matrix[row][col+3].getColor()
                        && !flags[row][col]
                        && !flags[row][col+1]
                        && !flags[row][col+2]
                        && !flags[row][col+3]){
                    count++;
                    for(int i=0;i<4;i++){
                        flags[row][col+i] = true;
                    }
                    row = Constants.SHELFCOLUMN - 2;
                }
            }
        }

        if(count>=4){
            addPlayer(p);
            return true;
        }
        else{
            return false;
        }
    }
}
package it.polimi.myShelfie.model.cards;
import it.polimi.myShelfie.model.Player;
import it.polimi.myShelfie.model.Tile;
import it.polimi.myShelfie.utilities.Settings;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
public class SharedGoal6Card extends SharedGoalCard implements CheckSharedGoal {
    private Stack<Integer> pointsTokenStack;
    private List<Player> achievedBy;

    public SharedGoal6Card(String imgPath, int playerNumber) {

        super(imgPath, playerNumber);
    }

    /**
     * Returns true if the player passed by parameter has achieved the shared goal
     * adds the player to the achievedBy list if needed
     * @return Check result
     */
    public boolean checkPattern(Player p){
        List<Tile.Color> colors = new ArrayList<>();
        int achievedRows = 0;
        if(!isAchieved(p)){
            for(int row = 0; row< Settings.SHELFROW; row++){
                for(int col = 0; col < Settings.SHELFCOLUMN; col++){

                    if((p.getMyShelf().getTileMartrix()[row][col].getColor()== Tile.Color.BLUE)&&(p.getMyShelf().getTileMartrix()[row][col].getColor()!=Tile.Color.NULLTILE)){
                        if(!colors.contains(Tile.Color.BLUE)){
                            colors.add(Tile.Color.BLUE);
                        }
                    }else if((p.getMyShelf().getTileMartrix()[row][col].getColor()== Tile.Color.GREEN)&&(p.getMyShelf().getTileMartrix()[row][col].getColor()!=Tile.Color.NULLTILE)){
                        if(!colors.contains(Tile.Color.GREEN)){
                            colors.add(Tile.Color.GREEN);
                        }
                    }else if((p.getMyShelf().getTileMartrix()[row][col].getColor()== Tile.Color.LIGHTBLUE)&&(p.getMyShelf().getTileMartrix()[row][col].getColor()!=Tile.Color.NULLTILE)){
                        if(!colors.contains(Tile.Color.LIGHTBLUE)){
                            colors.add(Tile.Color.LIGHTBLUE);
                        }
                    }else if((p.getMyShelf().getTileMartrix()[row][col].getColor()== Tile.Color.WHITE)&&(p.getMyShelf().getTileMartrix()[row][col].getColor()!=Tile.Color.NULLTILE)){
                        if(!colors.contains(Tile.Color.WHITE)){
                            colors.add(Tile.Color.WHITE);
                        }
                    }else if((p.getMyShelf().getTileMartrix()[row][col].getColor()== Tile.Color.YELLOW)&&(p.getMyShelf().getTileMartrix()[row][col].getColor()!=Tile.Color.NULLTILE)){
                        if(!colors.contains(Tile.Color.YELLOW)){
                            colors.add(Tile.Color.YELLOW);
                        }
                    }else if((p.getMyShelf().getTileMartrix()[row][col].getColor()== Tile.Color.PINK)&&(p.getMyShelf().getTileMartrix()[row][col].getColor()!=Tile.Color.NULLTILE)){
                        if(!colors.contains(Tile.Color.PINK)){
                            colors.add(Tile.Color.PINK);
                        }
                    }
                }
                if(colors.size()>=5){
                    achievedRows++;
                }
                colors.clear();
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

    @Override
    public String toString() {
        return "Card 6: You must have two full rows of your shelf made up with " +
                "tiles of different color";
    }
}
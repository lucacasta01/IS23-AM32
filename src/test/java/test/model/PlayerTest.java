package test.model;

import it.polimi.myShelfie.model.Player;
import it.polimi.myShelfie.model.Shelf;
import it.polimi.myShelfie.model.Tile;
import it.polimi.myShelfie.utilities.Settings;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlayerTest {

    @Test
    @DisplayName("Init shelf test")
    public void initShelfTest(){
        Player p = new Player("test");
        p.initShelf();
        for (int i=0; i< Settings.SHELFROW; i++){
            for(int j=0; j<Settings.SHELFCOLUMN; j++){
                assertEquals(p.getMyShelf().getTileMartrix()[i][j].getColor(), Tile.Color.NULLTILE);
            }
        }
    }

    @Test
    @DisplayName("Update score test")
    public void updateScoreTest(){
        Player p = new Player("test");
        p.setScore(2);
        p.updateScore(3);
        assertEquals(p.getScore(), 5);
    }
}

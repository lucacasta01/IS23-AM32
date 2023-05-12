package test;
import static org.junit.jupiter.api.Assertions.*;

import it.polimi.myShelfie.model.Board;
import it.polimi.myShelfie.model.Tile;
import it.polimi.myShelfie.utilities.Settings;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class BoardTest {
    @Test
    @DisplayName("needToRefill test")
    void needToRefillTest(){
        Board boardToRefill = new Board();
        Board boardNotToRefill = new Board();

        for(int i = 0; i< Settings.BOARD_DIM; i++){
            for(int j = 0; j< Settings.BOARD_DIM; j++){
                boardToRefill.getGrid()[i][j] = new Tile("fakePath", Tile.Color.NULLTILE);
                boardNotToRefill.getGrid()[i][j] = new Tile("fakePath", Tile.Color.NULLTILE);
            }
        }

        boardToRefill.getGrid()[3][3].setColor(Tile.Color.BLUE);
        boardToRefill.getGrid()[4][4].setColor(Tile.Color.BLUE);
        boardToRefill.getGrid()[6][3].setColor(Tile.Color.BLUE);
        boardToRefill.getGrid()[7][1].setColor(Tile.Color.BLUE);

        boardNotToRefill.getGrid()[3][3].setColor(Tile.Color.BLUE);
        boardNotToRefill.getGrid()[3][4].setColor(Tile.Color.LIGHTBLUE);
        boardNotToRefill.getGrid()[1][3].setColor(Tile.Color.BLUE);
        boardNotToRefill.getGrid()[1][5].setColor(Tile.Color.LIGHTBLUE);

        System.out.println(boardToRefill.toString());
        System.out.println(boardNotToRefill.toString());

        assertTrue(boardToRefill.needToRefill());
        assertFalse(boardNotToRefill.needToRefill());
    }

    @Test
    @DisplayName("isCatchable test")
    void isCatchableTest() {

    }
}

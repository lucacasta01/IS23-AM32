package test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

import it.polimi.myShelfie.model.Board;
import it.polimi.myShelfie.model.Position;
import it.polimi.myShelfie.model.Tile;
import it.polimi.myShelfie.model.cards.PersonalGoalCard;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class BoardTest {
    @Test
    @DisplayName("needToRefill test")
    void needToRefillTest(){
        Board boardToRefill = new Board();
        Board boardNotToRefill = new Board();

        boardToRefill.getGrid()[0][0].setColor(Tile.Color.BLUE);

        boardNotToRefill.getGrid()[0][0].setColor(Tile.Color.BLUE);
        boardNotToRefill.getGrid()[0][1].setColor(Tile.Color.LIGHTBLUE);

        assertTrue(boardToRefill.needToRefill());
        assertFalse(boardNotToRefill.needToRefill());
    }

    @Test
    @DisplayName("isCatchable test")
    void isCatchableTest() {

    }
}

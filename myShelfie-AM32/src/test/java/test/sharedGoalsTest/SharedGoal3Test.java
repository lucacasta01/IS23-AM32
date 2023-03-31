package test.sharedGoalsTest;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

import it.polimi.myShelfie.model.*;
import it.polimi.myShelfie.model.cards.*;
import it.polimi.myShelfie.utilities.Constants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SharedGoal3Test {
    @Test
    @DisplayName("checkPattern sharedGoal3 test")
    void checkPattern(){

        Tile[][] testingShelfAchieved = new Tile[Constants.SHELFROW][Constants.SHELFCOLUMN];
        for(int i=0;i<Constants.SHELFROW;i++){
            for(int j=0;j<Constants.SHELFCOLUMN;j++){
                testingShelfAchieved[i][j] = new Tile("",Tile.Color.NULLTILE);
            }
        }

        testingShelfAchieved[0][0].setColor(Tile.Color.BLUE);
        testingShelfAchieved[1][0].setColor(Tile.Color.BLUE);
        testingShelfAchieved[2][0].setColor(Tile.Color.BLUE);
        testingShelfAchieved[3][0].setColor(Tile.Color.BLUE);
        testingShelfAchieved[4][0].setColor(Tile.Color.PINK);
        testingShelfAchieved[5][0].setColor(Tile.Color.PINK);

        //testingShelfAchieved[0][1].setColor(Tile.Color.BLUE);
        testingShelfAchieved[1][1].setColor(Tile.Color.PINK);
        testingShelfAchieved[2][1].setColor(Tile.Color.BLUE);
        testingShelfAchieved[3][1].setColor(Tile.Color.LIGHTBLUE);
        testingShelfAchieved[4][1].setColor(Tile.Color.PINK);
        testingShelfAchieved[5][1].setColor(Tile.Color.GREEN);

        //testingShelfAchieved[0][2].setColor(Tile.Color.BLUE);
        testingShelfAchieved[1][2].setColor(Tile.Color.PINK);
        testingShelfAchieved[2][2].setColor(Tile.Color.BLUE);
        testingShelfAchieved[3][2].setColor(Tile.Color.LIGHTBLUE);
        testingShelfAchieved[4][2].setColor(Tile.Color.PINK);
        testingShelfAchieved[5][2].setColor(Tile.Color.LIGHTBLUE);

        //testingShelfAchieved[0][3].setColor(Tile.Color.YELLOW);
        testingShelfAchieved[1][3].setColor(Tile.Color.PINK);
        testingShelfAchieved[2][3].setColor(Tile.Color.BLUE);
        testingShelfAchieved[3][3].setColor(Tile.Color.LIGHTBLUE);
        testingShelfAchieved[4][3].setColor(Tile.Color.PINK);
        testingShelfAchieved[5][3].setColor(Tile.Color.GREEN);

        testingShelfAchieved[0][4].setColor(Tile.Color.PINK);
        testingShelfAchieved[1][4].setColor(Tile.Color.PINK);
        testingShelfAchieved[2][4].setColor(Tile.Color.PINK);
        testingShelfAchieved[3][4].setColor(Tile.Color.LIGHTBLUE);
        testingShelfAchieved[4][4].setColor(Tile.Color.GREEN);
        testingShelfAchieved[5][4].setColor(Tile.Color.GREEN);

        //expected 4 groups



        Tile[][] testingShelfNotAchieved = new Tile[Constants.SHELFROW][Constants.SHELFCOLUMN];
        for(int i=0;i<Constants.SHELFROW;i++){
            for(int j=0;j<Constants.SHELFCOLUMN;j++){
                testingShelfNotAchieved[i][j] = new Tile("",Tile.Color.NULLTILE);
            }
        }

        testingShelfNotAchieved[0][0].setColor(Tile.Color.BLUE);
        testingShelfNotAchieved[1][0].setColor(Tile.Color.BLUE);
        testingShelfNotAchieved[2][0].setColor(Tile.Color.PINK);
        testingShelfNotAchieved[3][0].setColor(Tile.Color.BLUE);
        testingShelfNotAchieved[4][0].setColor(Tile.Color.PINK);
        testingShelfNotAchieved[5][0].setColor(Tile.Color.PINK);

        //testingShelfNotAchieved[0][1].setColor(Tile.Color.BLUE);
        testingShelfNotAchieved[1][1].setColor(Tile.Color.PINK);
        testingShelfNotAchieved[2][1].setColor(Tile.Color.BLUE);
        testingShelfNotAchieved[3][1].setColor(Tile.Color.LIGHTBLUE);
        testingShelfNotAchieved[4][1].setColor(Tile.Color.PINK);
        testingShelfNotAchieved[5][1].setColor(Tile.Color.GREEN);

        //testingShelfNotAchieved[0][2].setColor(Tile.Color.BLUE);
        testingShelfNotAchieved[1][2].setColor(Tile.Color.PINK);
        testingShelfNotAchieved[2][2].setColor(Tile.Color.BLUE);
        testingShelfNotAchieved[3][2].setColor(Tile.Color.LIGHTBLUE);
        testingShelfNotAchieved[4][2].setColor(Tile.Color.PINK);
        testingShelfNotAchieved[5][2].setColor(Tile.Color.LIGHTBLUE);

        //testingShelfNotAchieved[0][3].setColor(Tile.Color.YELLOW);
        testingShelfNotAchieved[1][3].setColor(Tile.Color.PINK);
        testingShelfNotAchieved[2][3].setColor(Tile.Color.BLUE);
        testingShelfNotAchieved[3][3].setColor(Tile.Color.LIGHTBLUE);
        testingShelfNotAchieved[4][3].setColor(Tile.Color.PINK);
        testingShelfNotAchieved[5][3].setColor(Tile.Color.GREEN);

        testingShelfNotAchieved[0][4].setColor(Tile.Color.PINK);
        testingShelfNotAchieved[1][4].setColor(Tile.Color.PINK);
        testingShelfNotAchieved[2][4].setColor(Tile.Color.PINK);
        testingShelfNotAchieved[3][4].setColor(Tile.Color.LIGHTBLUE);
        testingShelfNotAchieved[4][4].setColor(Tile.Color.GREEN);
        testingShelfNotAchieved[5][4].setColor(Tile.Color.GREEN);


        Player p1 = new Player("player1","fakeAddress");
        p1.setMyShelf(new Shelf(testingShelfAchieved));

        Player p2 = new Player("player2","fakeAddress");
        p2.setMyShelf(new Shelf(testingShelfNotAchieved));

        SharedGoalCard card3 = new SharedGoal3Card("fakePath");

        assertTrue(card3.checkPattern(p1));
        assertFalse(card3.checkPattern(p2));

    }
}

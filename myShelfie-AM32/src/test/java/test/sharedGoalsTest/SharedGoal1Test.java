package test.sharedGoalsTest;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

import it.polimi.myShelfie.model.*;
import it.polimi.myShelfie.model.cards.PersonalGoalCard;
import it.polimi.myShelfie.model.cards.SharedGoal1Card;
import it.polimi.myShelfie.model.cards.SharedGoalCard;
import it.polimi.myShelfie.utilities.Constants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SharedGoal1Test {
    @Test
    @DisplayName("checkPattern sharedGoal1 test")
    void checkPattern(){
        Tile[][] testingShelfAchieved = new Tile[Constants.SHELFROW][Constants.SHELFCOLUMN];
        for(int i=0;i<Constants.SHELFROW;i++){
            for(int j=0;j<Constants.SHELFCOLUMN;j++){
                testingShelfAchieved[i][j] = new Tile("",Tile.Color.NULLTILE);
            }
        }

        testingShelfAchieved[0][0].setColor(Tile.Color.GREEN);
        testingShelfAchieved[0][1].setColor(Tile.Color.YELLOW);
        testingShelfAchieved[1][0].setColor(Tile.Color.WHITE);
        testingShelfAchieved[1][1].setColor(Tile.Color.PINK);

        testingShelfAchieved[3][3].setColor(Tile.Color.GREEN);
        testingShelfAchieved[3][4].setColor(Tile.Color.YELLOW);
        testingShelfAchieved[4][3].setColor(Tile.Color.WHITE);
        testingShelfAchieved[4][4].setColor(Tile.Color.PINK);

        /*
         * this testcase tests the edge situation when we have two 2x2 equals pattern but overlapped
         */
        Tile[][] testingShelfNotAchieved = new Tile[Constants.SHELFROW][Constants.SHELFCOLUMN];
        for(int i=0;i<Constants.SHELFROW;i++){
            for(int j=0;j<Constants.SHELFCOLUMN;j++){
                testingShelfNotAchieved[i][j] = new Tile("",Tile.Color.NULLTILE);
            }
        }

        testingShelfNotAchieved[0][0].setColor(Tile.Color.BLUE);
        testingShelfNotAchieved[0][1].setColor(Tile.Color.BLUE);
        testingShelfNotAchieved[1][0].setColor(Tile.Color.BLUE);
        testingShelfNotAchieved[1][1].setColor(Tile.Color.BLUE);

        testingShelfNotAchieved[0][1].setColor(Tile.Color.BLUE);
        testingShelfNotAchieved[0][2].setColor(Tile.Color.BLUE);
        testingShelfNotAchieved[1][1].setColor(Tile.Color.BLUE);
        testingShelfNotAchieved[1][2].setColor(Tile.Color.BLUE);


        Player p1 = new Player("player1","fakeAddress");
        p1.setMyShelf(new Shelf(testingShelfAchieved));

        Player p2 = new Player("player2","fakeAddress");
        p2.setMyShelf(new Shelf(testingShelfNotAchieved));

        SharedGoalCard card1 = new SharedGoal1Card("fakePath");

        assertTrue(card1.checkPattern(p1));
        assertFalse(card1.checkPattern(p2));

    }
}

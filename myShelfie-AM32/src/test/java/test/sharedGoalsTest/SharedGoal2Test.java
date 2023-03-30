package test.sharedGoalsTest;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

import it.polimi.myShelfie.model.*;
import it.polimi.myShelfie.model.cards.PersonalGoalCard;
import it.polimi.myShelfie.model.cards.SharedGoal1Card;
import it.polimi.myShelfie.model.cards.SharedGoal2Card;
import it.polimi.myShelfie.model.cards.SharedGoalCard;
import it.polimi.myShelfie.utilities.Constants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SharedGoal2Test {
    @Test
    @DisplayName("checkPattern sharedGoal2 test")
    void checkPattern(){

        Tile[][] testingShelfAchieved = new Tile[Constants.SHELFROW][Constants.SHELFCOLUMN];
        for(int i=0;i<Constants.SHELFROW;i++){
            for(int j=0;j<Constants.SHELFCOLUMN;j++){
                testingShelfAchieved[i][j] = new Tile("",Tile.Color.NULLTILE);
            }
        }

        testingShelfAchieved[0][0].setColor(Tile.Color.GREEN);
        testingShelfAchieved[1][0].setColor(Tile.Color.YELLOW);
        testingShelfAchieved[2][0].setColor(Tile.Color.WHITE);
        testingShelfAchieved[3][0].setColor(Tile.Color.PINK);
        testingShelfAchieved[4][0].setColor(Tile.Color.LIGHTBLUE);
        testingShelfAchieved[5][0].setColor(Tile.Color.BLUE);

        testingShelfAchieved[0][4].setColor(Tile.Color.BLUE);
        testingShelfAchieved[1][4].setColor(Tile.Color.PINK);
        testingShelfAchieved[2][4].setColor(Tile.Color.YELLOW);
        testingShelfAchieved[3][4].setColor(Tile.Color.LIGHTBLUE);
        testingShelfAchieved[4][4].setColor(Tile.Color.WHITE);
        testingShelfAchieved[5][4].setColor(Tile.Color.GREEN);



        Tile[][] testingShelfNotAchieved = new Tile[Constants.SHELFROW][Constants.SHELFCOLUMN];
        for(int i=0;i<Constants.SHELFROW;i++){
            for(int j=0;j<Constants.SHELFCOLUMN;j++){
                testingShelfNotAchieved[i][j] = new Tile("",Tile.Color.NULLTILE);
            }
        }

        testingShelfNotAchieved[0][3].setColor(Tile.Color.BLUE);
        testingShelfNotAchieved[1][3].setColor(Tile.Color.YELLOW);
        testingShelfNotAchieved[2][3].setColor(Tile.Color.PINK);
        testingShelfNotAchieved[3][3].setColor(Tile.Color.WHITE);
        testingShelfNotAchieved[4][3].setColor(Tile.Color.LIGHTBLUE);
        testingShelfNotAchieved[5][3].setColor(Tile.Color.GREEN);

        testingShelfNotAchieved[0][4].setColor(Tile.Color.BLUE);
        testingShelfNotAchieved[1][4].setColor(Tile.Color.GREEN);
        testingShelfNotAchieved[2][4].setColor(Tile.Color.BLUE);
        testingShelfNotAchieved[3][4].setColor(Tile.Color.WHITE);
        testingShelfNotAchieved[4][4].setColor(Tile.Color.LIGHTBLUE);
        testingShelfNotAchieved[5][4].setColor(Tile.Color.BLUE);

        testingShelfNotAchieved[0][2].setColor(Tile.Color.WHITE);
        testingShelfNotAchieved[1][2].setColor(Tile.Color.LIGHTBLUE);
        testingShelfNotAchieved[2][2].setColor(Tile.Color.BLUE);


        Player p1 = new Player("player1","fakeAddress");
        p1.setMyShelf(new Shelf(testingShelfAchieved));

        Player p2 = new Player("player2","fakeAddress");
        p2.setMyShelf(new Shelf(testingShelfNotAchieved));

        SharedGoalCard card2 = new SharedGoal2Card("fakePath");

        assertTrue(card2.checkPattern(p1));
        assertFalse(card2.checkPattern(p2));

    }
}

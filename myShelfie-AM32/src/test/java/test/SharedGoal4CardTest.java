package test;


import it.polimi.myShelfie.model.Game;
import it.polimi.myShelfie.model.Player;
import it.polimi.myShelfie.model.Shelf;
import it.polimi.myShelfie.model.Tile;
import it.polimi.myShelfie.model.cards.CheckSharedGoal;
import it.polimi.myShelfie.model.cards.SharedGoal4Card;
import it.polimi.myShelfie.utilities.Constants;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import it.polimi.myShelfie.utilities.Constants;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SharedGoal4CardTest {
    private final String fakeIP = "192.168.1.1";
    private final String fakeName = "testName";
    private final String fakePath = "fakePath";



    @Test
    @DisplayName("CheckSharedGoal 4 test")
    public void CheckSharedGoal4Test(){
        Player p1 = new Player(fakeName, fakeIP);
        SharedGoal4Card card = new SharedGoal4Card(fakePath);
        Tile[][] testingShelf = new Tile[Constants.SHELFROW][Constants.SHELFCOLUMN];
        for(int i=0;i<Constants.SHELFROW;i++){
            for(int j=0;j<Constants.SHELFCOLUMN;j++){
                testingShelf[i][j] = new Tile(fakePath,Tile.Color.NULLTILE);
            }
        }

        p1.setMyShelf(new Shelf(testingShelf));
        assertFalse(card.checkPattern(p1));

        testingShelf[0][0] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[0][1] = new Tile(fakePath, Tile.Color.BLUE);

        testingShelf[0][2] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[0][3] = new Tile(fakePath, Tile.Color.BLUE);

        testingShelf[1][0] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[1][1] = new Tile(fakePath, Tile.Color.BLUE);

        testingShelf[1][2] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[1][3] = new Tile(fakePath, Tile.Color.BLUE);

        testingShelf[2][0] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[2][1] = new Tile(fakePath, Tile.Color.BLUE);

        testingShelf[2][2] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[2][3] = new Tile(fakePath, Tile.Color.BLUE);

        p1.setMyShelf(new Shelf(testingShelf));

        assertTrue(card.checkPattern(p1));

        testingShelf[0][0] = new Tile(fakePath, Tile.Color.GREEN);
        testingShelf[1][0] = new Tile(fakePath, Tile.Color.GREEN);

        testingShelf[2][0] = new Tile(fakePath, Tile.Color.GREEN);
        testingShelf[3][0] = new Tile(fakePath, Tile.Color.GREEN);

        testingShelf[0][1] = new Tile(fakePath, Tile.Color.GREEN);
        testingShelf[1][1] = new Tile(fakePath, Tile.Color.GREEN);

        testingShelf[2][1] = new Tile(fakePath, Tile.Color.GREEN);
        testingShelf[3][1] = new Tile(fakePath, Tile.Color.GREEN);

        testingShelf[0][2] = new Tile(fakePath, Tile.Color.GREEN);
        testingShelf[1][2] = new Tile(fakePath, Tile.Color.GREEN);

        testingShelf[2][2] = new Tile(fakePath, Tile.Color.GREEN);
        testingShelf[3][2] = new Tile(fakePath, Tile.Color.GREEN);

        p1.setMyShelf(new Shelf(testingShelf));
        assertTrue(card.checkPattern(p1));




    }




}

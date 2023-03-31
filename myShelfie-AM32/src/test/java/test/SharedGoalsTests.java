package test;


import it.polimi.myShelfie.model.Player;
import it.polimi.myShelfie.model.Shelf;
import it.polimi.myShelfie.model.Tile;
import it.polimi.myShelfie.model.cards.*;
import it.polimi.myShelfie.utilities.Constants;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

public class SharedGoalsTests {
    private final String fakeIP = "192.168.1.1";
    private final String fakeName = "testName";
    private final String fakePath = "fakePath";



    @Test
    @DisplayName("CheckSharedGoal 1 test")
    public void CheckSharedGoal1test(){
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

    @Test
    @DisplayName("CheckSharedGoal 2 test")
    public void checkSharedGoal2test(){

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

    @Test
    @DisplayName("CheckSharedGoal 3 test")
    public void checkSharedGoal3test(){

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

    @Test
    @DisplayName("CheckSharedGoal 7 test")
    public void checkSharedGoal7test(){
        Player p1 = new Player(fakeName, fakeIP);
        SharedGoal7Card testCard = new SharedGoal7Card(fakePath);
        Tile[][] testingShelf = new Tile[Constants.SHELFROW][Constants.SHELFCOLUMN];
        for(int i=0;i<Constants.SHELFROW;i++){
            for(int j=0;j<Constants.SHELFCOLUMN;j++){
                testingShelf[i][j] = new Tile(fakePath,Tile.Color.NULLTILE);
            }
        }
        p1.setMyShelf(new Shelf(testingShelf));
        assertFalse(testCard.checkPattern(p1));

        testingShelf[0][0] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[0][1] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[0][2] = new Tile(fakePath, Tile.Color.LIGHTBLUE);
        testingShelf[0][3] = new Tile(fakePath, Tile.Color.GREEN);
        testingShelf[0][4] = new Tile(fakePath, Tile.Color.PINK);

        testingShelf[1][0] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[1][1] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[1][2] = new Tile(fakePath, Tile.Color.LIGHTBLUE);
        testingShelf[1][3] = new Tile(fakePath, Tile.Color.GREEN);
        testingShelf[1][4] = new Tile(fakePath, Tile.Color.PINK);

        testingShelf[2][0] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[2][1] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[2][2] = new Tile(fakePath, Tile.Color.LIGHTBLUE);
        testingShelf[2][3] = new Tile(fakePath, Tile.Color.GREEN);
        testingShelf[2][4] = new Tile(fakePath, Tile.Color.PINK);

        p1.setMyShelf(new Shelf(testingShelf));
        assertTrue(testCard.checkPattern(p1));

        testingShelf[0][0] = new Tile(fakePath, Tile.Color.NULLTILE);

        p1.setMyShelf(new Shelf(testingShelf));
        assertFalse(testCard.checkPattern(p1));
    }


    @Test
    @DisplayName("CheckSharedGoal 8 test")
    public void checkSharedGoal8test(){
        Player p1 = new Player(fakeName, fakeIP);
        SharedGoal8Card testCard = new SharedGoal8Card(fakePath);
        Tile[][] testingShelf = new Tile[Constants.SHELFROW][Constants.SHELFCOLUMN];
        for(int i=0;i<Constants.SHELFROW;i++){
            for(int j=0;j<Constants.SHELFCOLUMN;j++){
                testingShelf[i][j] = new Tile(fakePath,Tile.Color.NULLTILE);
            }
        }
        testingShelf[0][0] = new Tile(fakePath, Tile.Color.BLUE);
        p1.setMyShelf(new Shelf(testingShelf));
        assertFalse(testCard.checkPattern(p1));

        testingShelf[Constants.SHELFROW-1][Constants.SHELFCOLUMN-1] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[0][Constants.SHELFCOLUMN-1] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[Constants.SHELFROW-1][0] = new Tile(fakePath, Tile.Color.BLUE);

        p1.setMyShelf(new Shelf(testingShelf));
        assertTrue(testCard.checkPattern(p1));
    }
    @Test
    @DisplayName("CheckSharedGoal 9 test")
    public void checkSharedGoal9test(){
        Player p1 = new Player(fakeName, fakeIP);
        SharedGoal9Card testCard = new SharedGoal9Card(fakePath);
        Tile[][] testingShelf = new Tile[Constants.SHELFROW][Constants.SHELFCOLUMN];
        for(int i=0;i<Constants.SHELFROW;i++){
            for(int j=0;j<Constants.SHELFCOLUMN;j++){
                testingShelf[i][j] = new Tile(fakePath,Tile.Color.NULLTILE);
            }
        }
        testingShelf[0][0] = new Tile(fakePath, Tile.Color.BLUE);
        p1.setMyShelf(new Shelf(testingShelf));
        assertFalse(testCard.checkPattern(p1));

        for(int i=0;i<3;i++){
            for(int j=0;j<Constants.SHELFCOLUMN;j++){
                testingShelf[i][j] = new Tile(fakePath,Tile.Color.BLUE);
            }
        }
        p1.setMyShelf(new Shelf(testingShelf));
        assertTrue(testCard.checkPattern(p1));
    }



}

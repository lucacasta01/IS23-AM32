package test.model;


import it.polimi.myShelfie.model.Game;
import it.polimi.myShelfie.model.Player;
import it.polimi.myShelfie.model.Shelf;
import it.polimi.myShelfie.model.Tile;
import it.polimi.myShelfie.model.cards.*;
import it.polimi.myShelfie.utilities.Settings;
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
        int playerNumber = 2;
        Tile[][] testingShelfAchieved = new Tile[Settings.SHELFROW][Settings.SHELFCOLUMN];
        for(int i = 0; i< Settings.SHELFROW; i++){
            for(int j = 0; j< Settings.SHELFCOLUMN; j++){
                testingShelfAchieved[i][j] = new Tile("",Tile.Color.NULLTILE);
            }
        }

        testingShelfAchieved[0][0].setColor(Tile.Color.PINK);
        testingShelfAchieved[0][1].setColor(Tile.Color.PINK);
        testingShelfAchieved[1][0].setColor(Tile.Color.PINK);
        testingShelfAchieved[1][1].setColor(Tile.Color.PINK);

        testingShelfAchieved[3][3].setColor(Tile.Color.PINK);
        testingShelfAchieved[3][4].setColor(Tile.Color.PINK);
        testingShelfAchieved[4][3].setColor(Tile.Color.PINK);
        testingShelfAchieved[4][4].setColor(Tile.Color.PINK);

        /*
         * this testcase tests the edge situation when we have two 2x2 equals pattern but overlapped
         */
        Tile[][] testingShelfNotAchieved = new Tile[Settings.SHELFROW][Settings.SHELFCOLUMN];
        for(int i = 0; i< Settings.SHELFROW; i++){
            for(int j = 0; j< Settings.SHELFCOLUMN; j++){
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

        for(int i=0;i<Settings.SHELFROW;i++){
            for(int j=0;j<Settings.SHELFCOLUMN;j++){
                System.out.print(testingShelfAchieved[i][j].toString()+" ");
            }
            System.out.println("");
        }

        System.out.println("");

        for(int i=0;i<Settings.SHELFROW;i++){
            for(int j=0;j<Settings.SHELFCOLUMN;j++){
                System.out.print(testingShelfNotAchieved[i][j].toString()+" ");
            }
            System.out.println("");
        }


        Player p1 = new Player("player1");
        p1.setMyShelf(new Shelf(testingShelfAchieved));

        Player p2 = new Player("player2");
        p2.setMyShelf(new Shelf(testingShelfNotAchieved));

        SharedGoalCard card1 = new SharedGoal1Card("fakePath", playerNumber);

        assertTrue(card1.checkPattern(p1));
        assertFalse(card1.checkPattern(p1));
        assertFalse(card1.checkPattern(p2));

    }

    @Test
    @DisplayName("CheckSharedGoal 2 test")
    public void checkSharedGoal2test(){
        int playerNumber = 2;
        Tile[][] testingShelfAchieved = new Tile[Settings.SHELFROW][Settings.SHELFCOLUMN];
        for(int i = 0; i< Settings.SHELFROW; i++){
            for(int j = 0; j< Settings.SHELFCOLUMN; j++){
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
        Tile[][] testingShelfNotAchieved = new Tile[Settings.SHELFROW][Settings.SHELFCOLUMN];
        for(int i = 0; i< Settings.SHELFROW; i++){
            for(int j = 0; j< Settings.SHELFCOLUMN; j++){
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


        Player p1 = new Player("player1");
        p1.setMyShelf(new Shelf(testingShelfAchieved));

        Player p2 = new Player("player2");
        p2.setMyShelf(new Shelf(testingShelfNotAchieved));

        SharedGoalCard card2 = new SharedGoal2Card("fakePath", playerNumber);

        assertTrue(card2.checkPattern(p1));
        assertFalse(card2.checkPattern(p2));

    }

    @Test
    @DisplayName("CheckSharedGoal 3 test")
    public void checkSharedGoal3test(){
        int playerNumber = 2;
        Tile[][] testingShelfAchieved = new Tile[Settings.SHELFROW][Settings.SHELFCOLUMN];
        for(int i = 0; i< Settings.SHELFROW; i++){
            for(int j = 0; j< Settings.SHELFCOLUMN; j++){
                testingShelfAchieved[i][j] = new Tile("",Tile.Color.NULLTILE);
            }
        }

        //testingShelfAchieved[0][0].setColor(Tile.Color.);
        //testingShelfAchieved[1][0].setColor(Tile.Color.);
        //testingShelfAchieved[2][0].setColor(Tile.Color.);
        //testingShelfAchieved[3][0].setColor(Tile.Color.);
        testingShelfAchieved[4][0].setColor(Tile.Color.YELLOW);
        testingShelfAchieved[5][0].setColor(Tile.Color.BLUE);

        //testingShelfAchieved[0][1].setColor(Tile.Color.);
        //testingShelfAchieved[1][1].setColor(Tile.Color.);
        //testingShelfAchieved[2][1].setColor(Tile.Color.);
        //testingShelfAchieved[3][1].setColor(Tile.Color.);
        testingShelfAchieved[4][1].setColor(Tile.Color.BLUE);
        testingShelfAchieved[5][1].setColor(Tile.Color.BLUE);

        //testingShelfAchieved[0][2].setColor(Tile.Color.);
        testingShelfAchieved[1][2].setColor(Tile.Color.PINK);
        testingShelfAchieved[2][2].setColor(Tile.Color.PINK);
        testingShelfAchieved[3][2].setColor(Tile.Color.GREEN);
        testingShelfAchieved[4][2].setColor(Tile.Color.BLUE);
        testingShelfAchieved[5][2].setColor(Tile.Color.WHITE);

        //testingShelfAchieved[0][3].setColor(Tile.Color.);
        testingShelfAchieved[1][3].setColor(Tile.Color.PINK);
        testingShelfAchieved[2][3].setColor(Tile.Color.PINK);
        testingShelfAchieved[3][3].setColor(Tile.Color.WHITE);
        testingShelfAchieved[4][3].setColor(Tile.Color.WHITE);
        testingShelfAchieved[5][3].setColor(Tile.Color.WHITE);

        //testingShelfAchieved[0][4].setColor(Tile.Color.);
        //testingShelfAchieved[1][4].setColor(Tile.Color.);
        testingShelfAchieved[2][4].setColor(Tile.Color.GREEN);
        testingShelfAchieved[3][4].setColor(Tile.Color.GREEN);
        testingShelfAchieved[4][4].setColor(Tile.Color.GREEN);
        testingShelfAchieved[5][4].setColor(Tile.Color.GREEN);

        //expected 4 groups


        Tile[][] testingShelfNotAchieved = new Tile[Settings.SHELFROW][Settings.SHELFCOLUMN];
        for(int i = 0; i< Settings.SHELFROW; i++){
            for(int j = 0; j< Settings.SHELFCOLUMN; j++){
                testingShelfNotAchieved[i][j] = new Tile("",Tile.Color.NULLTILE);
            }
        }

        //testingShelfNotAchieved[0][0].setColor(Tile.Color.);
        //testingShelfNotAchieved[1][0].setColor(Tile.Color.);
        testingShelfNotAchieved[2][0].setColor(Tile.Color.PINK);
        testingShelfNotAchieved[3][0].setColor(Tile.Color.PINK);
        testingShelfNotAchieved[4][0].setColor(Tile.Color.BLUE);
        testingShelfNotAchieved[5][0].setColor(Tile.Color.BLUE);

        //testingShelfNotAchieved[0][1].setColor(Tile.Color.);
        testingShelfNotAchieved[1][1].setColor(Tile.Color.LIGHTBLUE);
        testingShelfNotAchieved[2][1].setColor(Tile.Color.PINK);
        testingShelfNotAchieved[3][1].setColor(Tile.Color.LIGHTBLUE);
        testingShelfNotAchieved[4][1].setColor(Tile.Color.GREEN);
        testingShelfNotAchieved[5][1].setColor(Tile.Color.BLUE);

        //testingShelfNotAchieved[0][2].setColor(Tile.Color.);
        testingShelfNotAchieved[1][2].setColor(Tile.Color.WHITE);
        testingShelfNotAchieved[2][2].setColor(Tile.Color.GREEN);
        testingShelfNotAchieved[3][2].setColor(Tile.Color.GREEN);
        testingShelfNotAchieved[4][2].setColor(Tile.Color.GREEN);
        testingShelfNotAchieved[5][2].setColor(Tile.Color.BLUE);

        testingShelfNotAchieved[0][3].setColor(Tile.Color.WHITE);
        testingShelfNotAchieved[1][3].setColor(Tile.Color.WHITE);
        testingShelfNotAchieved[2][3].setColor(Tile.Color.YELLOW);
        testingShelfNotAchieved[3][3].setColor(Tile.Color.YELLOW);
        testingShelfNotAchieved[4][3].setColor(Tile.Color.YELLOW);
        testingShelfNotAchieved[5][3].setColor(Tile.Color.YELLOW);

        testingShelfNotAchieved[0][4].setColor(Tile.Color.WHITE);
        testingShelfNotAchieved[1][4].setColor(Tile.Color.WHITE);
        testingShelfNotAchieved[2][4].setColor(Tile.Color.PINK);
        testingShelfNotAchieved[3][4].setColor(Tile.Color.PINK);
        testingShelfNotAchieved[4][4].setColor(Tile.Color.PINK);
        testingShelfNotAchieved[5][4].setColor(Tile.Color.GREEN);


        Player p1 = new Player("player1");
        p1.setMyShelf(new Shelf(testingShelfAchieved));

        Player p2 = new Player("player2");
        p2.setMyShelf(new Shelf(testingShelfNotAchieved));

        SharedGoalCard card3 = new SharedGoal3Card("fakePath", playerNumber);

        assertTrue(card3.checkPattern(p1));
        assertFalse(card3.checkPattern(p1)); // p1 has already achieved the goal
        assertFalse(card3.checkPattern(p2));

    }
    @Test
    @DisplayName("CheckSharedGoal 4 test")
    public void CheckSharedGoal4Test(){
        int playerNumber = 2;
        Player p1 = new Player(fakeName);
        SharedGoal4Card card = new SharedGoal4Card(fakePath, playerNumber);
        Tile[][] testingShelf = new Tile[Settings.SHELFROW][Settings.SHELFCOLUMN];
        for(int i = 0; i< Settings.SHELFROW; i++){
            for(int j = 0; j< Settings.SHELFCOLUMN; j++){
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

        resetMatrix(testingShelf);
        Player p2 = new Player(fakeName);

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

        p2.setMyShelf(new Shelf(testingShelf));
        assertTrue(card.checkPattern(p2));
    }


    @Test
    @DisplayName("CheckSharedGoal 5 test")
    public void CheckSharedGoal5Test(){
        int playerNumber = 2;
        Player p1 = new Player(fakeName);
        SharedGoal5Card card = new SharedGoal5Card(fakePath, playerNumber);
        Tile[][] testingShelf = new Tile[Settings.SHELFROW][Settings.SHELFCOLUMN];
        for(int i = 0; i< Settings.SHELFROW; i++){
            for(int j = 0; j< Settings.SHELFCOLUMN; j++){
                testingShelf[i][j] = new Tile(fakePath,Tile.Color.NULLTILE);
            }
        }

        p1.setMyShelf(new Shelf(testingShelf));

        assertFalse(card.checkPattern(p1));

        testingShelf[0][0] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[1][0] = new Tile(fakePath, Tile.Color.YELLOW);
        testingShelf[2][0] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[3][0] = new Tile(fakePath, Tile.Color.GREEN);
        testingShelf[4][0] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[5][0] = new Tile(fakePath, Tile.Color.YELLOW);

        testingShelf[0][1] = new Tile(fakePath, Tile.Color.WHITE);
        testingShelf[1][1] = new Tile(fakePath, Tile.Color.PINK);
        testingShelf[2][1] = new Tile(fakePath, Tile.Color.LIGHTBLUE);
        testingShelf[3][1] = new Tile(fakePath, Tile.Color.WHITE);
        testingShelf[4][1] = new Tile(fakePath, Tile.Color.LIGHTBLUE);
        testingShelf[5][1] = new Tile(fakePath, Tile.Color.PINK);

        testingShelf[0][2] = new Tile(fakePath, Tile.Color.GREEN);
        testingShelf[1][2] = new Tile(fakePath, Tile.Color.GREEN);
        testingShelf[2][2] = new Tile(fakePath, Tile.Color.WHITE);
        testingShelf[3][2] = new Tile(fakePath, Tile.Color.GREEN);
        testingShelf[4][2] = new Tile(fakePath, Tile.Color.GREEN);
        testingShelf[5][2] = new Tile(fakePath, Tile.Color.LIGHTBLUE);

        p1.setMyShelf(new Shelf(testingShelf));
        assertTrue(card.checkPattern(p1));

        resetMatrix(testingShelf);

        testingShelf[0][0] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[1][0] = new Tile(fakePath, Tile.Color.YELLOW);
        testingShelf[2][0] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[3][0] = new Tile(fakePath, Tile.Color.GREEN);
        testingShelf[4][0] = new Tile(fakePath, Tile.Color.LIGHTBLUE);
        testingShelf[5][0] = new Tile(fakePath, Tile.Color.YELLOW);

        testingShelf[0][1] = new Tile(fakePath, Tile.Color.WHITE);
        testingShelf[1][1] = new Tile(fakePath, Tile.Color.PINK);
        testingShelf[2][1] = new Tile(fakePath, Tile.Color.LIGHTBLUE);
        testingShelf[3][1] = new Tile(fakePath, Tile.Color.WHITE);
        testingShelf[4][1] = new Tile(fakePath, Tile.Color.LIGHTBLUE);
        testingShelf[5][1] = new Tile(fakePath, Tile.Color.PINK);

        testingShelf[0][2] = new Tile(fakePath, Tile.Color.NULLTILE);
        testingShelf[1][2] = new Tile(fakePath, Tile.Color.GREEN);
        testingShelf[2][2] = new Tile(fakePath, Tile.Color.WHITE);
        testingShelf[3][2] = new Tile(fakePath, Tile.Color.GREEN);
        testingShelf[4][2] = new Tile(fakePath, Tile.Color.GREEN);
        testingShelf[5][2] = new Tile(fakePath, Tile.Color.LIGHTBLUE);

        p1.setMyShelf(new Shelf(testingShelf));
        assertFalse(card.checkPattern(p1));
    }

    @Test
    @DisplayName("CheckSharedGoal 6 test")
    public void CheckSharedGoal6Test(){
        int playerNumber = 2;
        Player p1 = new Player(fakeName);
        SharedGoal6Card card = new SharedGoal6Card(fakePath, playerNumber);
        Tile[][] testingShelf = new Tile[Settings.SHELFROW][Settings.SHELFCOLUMN];
        for(int i = 0; i< Settings.SHELFROW; i++){
            for(int j = 0; j< Settings.SHELFCOLUMN; j++){
                testingShelf[i][j] = new Tile(fakePath,Tile.Color.NULLTILE);
            }
        }

        p1.setMyShelf(new Shelf(testingShelf));

        assertFalse(card.checkPattern(p1));


        testingShelf[0][0] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[0][1] = new Tile(fakePath, Tile.Color.YELLOW);
        testingShelf[0][2] = new Tile(fakePath, Tile.Color.LIGHTBLUE);
        testingShelf[0][3] = new Tile(fakePath, Tile.Color.GREEN);
        testingShelf[0][4] = new Tile(fakePath, Tile.Color.WHITE);


        testingShelf[1][0] = new Tile(fakePath, Tile.Color.WHITE);
        testingShelf[1][1] = new Tile(fakePath, Tile.Color.PINK);
        testingShelf[1][2] = new Tile(fakePath, Tile.Color.LIGHTBLUE);
        testingShelf[1][3] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[1][4] = new Tile(fakePath, Tile.Color.GREEN);


        testingShelf[2][0] = new Tile(fakePath, Tile.Color.GREEN);
        testingShelf[2][1] = new Tile(fakePath, Tile.Color.YELLOW);
        testingShelf[2][2] = new Tile(fakePath, Tile.Color.WHITE);
        testingShelf[2][3] = new Tile(fakePath, Tile.Color.LIGHTBLUE);
        testingShelf[2][4] = new Tile(fakePath, Tile.Color.BLUE);



        p1.setMyShelf(new Shelf(testingShelf));
        assertTrue(card.checkPattern(p1));

        resetMatrix(testingShelf);

        testingShelf[0][0] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[0][1] = new Tile(fakePath, Tile.Color.YELLOW);
        testingShelf[0][2] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[0][3] = new Tile(fakePath, Tile.Color.GREEN);
        testingShelf[0][4] = new Tile(fakePath, Tile.Color.LIGHTBLUE);


        testingShelf[1][0] = new Tile(fakePath, Tile.Color.WHITE);
        testingShelf[1][1] = new Tile(fakePath, Tile.Color.PINK);
        testingShelf[1][2] = new Tile(fakePath, Tile.Color.LIGHTBLUE);
        testingShelf[1][3] = new Tile(fakePath, Tile.Color.WHITE);
        testingShelf[1][4] = new Tile(fakePath, Tile.Color.LIGHTBLUE);


        testingShelf[2][0] = new Tile(fakePath, Tile.Color.NULLTILE);
        testingShelf[2][1] = new Tile(fakePath, Tile.Color.GREEN);
        testingShelf[2][2] = new Tile(fakePath, Tile.Color.WHITE);
        testingShelf[2][3] = new Tile(fakePath, Tile.Color.GREEN);
        testingShelf[2][4] = new Tile(fakePath, Tile.Color.GREEN);


        p1.setMyShelf(new Shelf(testingShelf));
        assertFalse(card.checkPattern(p1));
    }



    @Test
    @DisplayName("CheckSharedGoal 7 test")
    public void checkSharedGoal7test(){
        int playerNumber = 2;
        Player p1 = new Player(fakeName);
        SharedGoal7Card testCard = new SharedGoal7Card(fakePath, playerNumber);
        Tile[][] testingShelf = new Tile[Settings.SHELFROW][Settings.SHELFCOLUMN];
        for(int i = 0; i< Settings.SHELFROW; i++){
            for(int j = 0; j< Settings.SHELFCOLUMN; j++){
                testingShelf[i][j] = new Tile(fakePath,Tile.Color.NULLTILE);
            }
        }
        p1.setMyShelf(new Shelf(testingShelf));
        assertFalse(testCard.checkPattern(p1));

        testingShelf[0][0] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[0][1] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[0][2] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[0][3] = new Tile(fakePath, Tile.Color.GREEN);
        testingShelf[0][4] = new Tile(fakePath, Tile.Color.PINK);

        testingShelf[1][0] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[1][1] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[1][2] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[1][3] = new Tile(fakePath, Tile.Color.GREEN);
        testingShelf[1][4] = new Tile(fakePath, Tile.Color.PINK);

        testingShelf[2][0] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[2][1] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[2][2] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[2][3] = new Tile(fakePath, Tile.Color.GREEN);
        testingShelf[2][4] = new Tile(fakePath, Tile.Color.PINK);

        testingShelf[4][0] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[4][1] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[4][2] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[4][3] = new Tile(fakePath, Tile.Color.GREEN);
        testingShelf[4][4] = new Tile(fakePath, Tile.Color.PINK);

        p1.setMyShelf(new Shelf(testingShelf));
        assertTrue(testCard.checkPattern(p1));


        testingShelf[0][0] = new Tile(fakePath, Tile.Color.NULLTILE);

        p1.setMyShelf(new Shelf(testingShelf));
        assertFalse(testCard.checkPattern(p1));

        resetMatrix(testingShelf);

        testingShelf[0][0] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[0][1] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[0][2] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[0][3] = new Tile(fakePath, Tile.Color.GREEN);
        testingShelf[0][4] = new Tile(fakePath, Tile.Color.PINK);

        testingShelf[1][0] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[1][1] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[1][2] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[1][3] = new Tile(fakePath, Tile.Color.GREEN);
        testingShelf[1][4] = new Tile(fakePath, Tile.Color.PINK);

        testingShelf[2][0] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[2][1] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[2][2] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[2][3] = new Tile(fakePath, Tile.Color.GREEN);
        testingShelf[2][4] = new Tile(fakePath, Tile.Color.PINK);

        testingShelf[4][0] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[4][1] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[4][2] = new Tile(fakePath, Tile.Color.LIGHTBLUE);
        testingShelf[4][3] = new Tile(fakePath, Tile.Color.GREEN);
        testingShelf[4][4] = new Tile(fakePath, Tile.Color.PINK);

        p1.setMyShelf(new Shelf(testingShelf));
        assertFalse(testCard.checkPattern(p1));

    }


    @Test
    @DisplayName("CheckSharedGoal 8 test")
    public void checkSharedGoal8test(){
        int playerNumber = 2;
        Player p1 = new Player(fakeName);
        SharedGoal8Card testCard = new SharedGoal8Card(fakePath, playerNumber);
        Tile[][] testingShelf = new Tile[Settings.SHELFROW][Settings.SHELFCOLUMN];
        for(int i = 0; i< Settings.SHELFROW; i++){
            for(int j = 0; j< Settings.SHELFCOLUMN; j++){
                testingShelf[i][j] = new Tile(fakePath,Tile.Color.NULLTILE);
            }
        }
        testingShelf[0][0] = new Tile(fakePath, Tile.Color.BLUE);
        p1.setMyShelf(new Shelf(testingShelf));
        assertFalse(testCard.checkPattern(p1));

        testingShelf[Settings.SHELFROW-1][Settings.SHELFCOLUMN-1] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[0][Settings.SHELFCOLUMN-1] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[Settings.SHELFROW-1][0] = new Tile(fakePath, Tile.Color.BLUE);

        p1.setMyShelf(new Shelf(testingShelf));
        assertTrue(testCard.checkPattern(p1));
    }
    @Test
    @DisplayName("CheckSharedGoal 9 test")
    public void checkSharedGoal9test(){
        int playerNumber = 2;
        Player p1 = new Player(fakeName);
        SharedGoal9Card testCard = new SharedGoal9Card(fakePath, playerNumber);
        Tile[][] testingShelf = new Tile[Settings.SHELFROW][Settings.SHELFCOLUMN];
        for(int i = 0; i< Settings.SHELFROW; i++){
            for(int j = 0; j< Settings.SHELFCOLUMN; j++){
                testingShelf[i][j] = new Tile(fakePath,Tile.Color.NULLTILE);
            }
        }
        testingShelf[0][0] = new Tile(fakePath, Tile.Color.BLUE);
        p1.setMyShelf(new Shelf(testingShelf));
        assertFalse(testCard.checkPattern(p1));

        for(int i=0;i<3;i++){
            for(int j = 0; j< Settings.SHELFCOLUMN; j++){
                testingShelf[i][j] = new Tile(fakePath,Tile.Color.BLUE);
            }
        }
        p1.setMyShelf(new Shelf(testingShelf));
        assertTrue(testCard.checkPattern(p1));
    }

    @Test
    @DisplayName("CheckSharedGoal 10 test")
    public void checkSharedGoal10test(){
        int playerNumber = 2;
        Player p1 = new Player(fakeName);
        SharedGoal10Card testCard = new SharedGoal10Card(fakePath, playerNumber);
        Tile[][] testingShelf = new Tile[Settings.SHELFROW][Settings.SHELFCOLUMN];
        for(int i = 0; i< Settings.SHELFROW; i++){
            for(int j = 0; j< Settings.SHELFCOLUMN; j++){
                testingShelf[i][j] = new Tile(fakePath,Tile.Color.NULLTILE);
            }
        }

        p1.setMyShelf(new Shelf(testingShelf));
        assertFalse(testCard.checkPattern(p1));


        testingShelf[0][0] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[0][2] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[1][1] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[2][0] = new Tile(fakePath, Tile.Color.WHITE);
        testingShelf[2][2] = new Tile(fakePath, Tile.Color.WHITE);
        testingShelf[3][1] = new Tile(fakePath, Tile.Color.WHITE);
        testingShelf[4][0] = new Tile(fakePath, Tile.Color.WHITE);
        testingShelf[4][2] = new Tile(fakePath, Tile.Color.WHITE);

        p1.setMyShelf(new Shelf(testingShelf));
        assertTrue(testCard.checkPattern(p1));

        resetMatrix(testingShelf);

        Player p2 = new Player(fakeName);

        testingShelf[0][0] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[0][2] = new Tile(fakePath, Tile.Color.PINK);
        testingShelf[1][1] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[1][3] = new Tile(fakePath, Tile.Color.WHITE);
        testingShelf[2][0] = new Tile(fakePath, Tile.Color.WHITE);
        testingShelf[2][2] = new Tile(fakePath, Tile.Color.WHITE);
        testingShelf[3][1] = new Tile(fakePath, Tile.Color.YELLOW);
        testingShelf[3][3] = new Tile(fakePath, Tile.Color.WHITE);
        testingShelf[3][2] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[3][4] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[4][3] = new Tile(fakePath, Tile.Color.LIGHTBLUE);
        testingShelf[5][2] = new Tile(fakePath, Tile.Color.WHITE);
        testingShelf[5][4] = new Tile(fakePath, Tile.Color.WHITE);

        p2.setMyShelf(new Shelf(testingShelf));
        assertFalse(testCard.checkPattern(p2));
    }
    @Test
    @DisplayName("CheckSharedGoal 11 test")
    public void checkSharedGoal11test(){
        int playerNumber = 2;
        Player p1 = new Player(fakeName);
        SharedGoal11Card testCard = new SharedGoal11Card(fakePath, playerNumber);
        Tile[][] testingShelf = new Tile[Settings.SHELFROW][Settings.SHELFCOLUMN];
        for(int i = 0; i< Settings.SHELFROW; i++){
            for(int j = 0; j< Settings.SHELFCOLUMN; j++){
                testingShelf[i][j] = new Tile(fakePath,Tile.Color.NULLTILE);
            }
        }
        p1.setMyShelf(new Shelf(testingShelf));
        assertFalse(testCard.checkPattern(p1));

        resetMatrix(testingShelf);

        testingShelf[0][0] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[1][1] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[2][2] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[3][3] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[4][4] = new Tile(fakePath, Tile.Color.BLUE);

        p1.setMyShelf(new Shelf(testingShelf));
        assertTrue(testCard.checkPattern(p1));

        resetMatrix(testingShelf);

        Player p2 = new Player(fakeName);

        testingShelf[1][0] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[2][1] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[3][2] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[4][3] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[5][4] = new Tile(fakePath, Tile.Color.BLUE);

        p2.setMyShelf(new Shelf(testingShelf));
        assertTrue(testCard.checkPattern(p2));

        resetMatrix(testingShelf);

        Player p3 = new Player(fakeName);

        testingShelf[0][0] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[1][1] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[2][2] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[3][3] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[4][4] = new Tile(fakePath, Tile.Color.BLUE);

        p3.setMyShelf(new Shelf(testingShelf));
        assertTrue(testCard.checkPattern(p3));

        resetMatrix(testingShelf);

        Player p4 = new Player(fakeName);

        testingShelf[1][0] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[2][1] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[3][2] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[4][3] = new Tile(fakePath, Tile.Color.LIGHTBLUE);
        testingShelf[5][4] = new Tile(fakePath, Tile.Color.BLUE);

        p4.setMyShelf(new Shelf(testingShelf));
        assertFalse(testCard.checkPattern(p4));

        Player p5 = new Player(fakeName);

        testingShelf[0][1] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[1][2] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[2][2] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[3][3] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[4][4] = new Tile(fakePath, Tile.Color.BLUE);

        p5.setMyShelf(new Shelf(testingShelf));
        assertFalse(testCard.checkPattern(p5));

        Player p6 = new Player(fakeName);

        testingShelf[0][4] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[1][3] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[2][2] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[3][1] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[4][0] = new Tile(fakePath, Tile.Color.BLUE);

        p6.setMyShelf(new Shelf(testingShelf));
        assertTrue(testCard.checkPattern(p6));

        Player p7 = new Player(fakeName);

        testingShelf[1][4] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[2][3] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[3][2] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[4][1] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[5][0] = new Tile(fakePath, Tile.Color.BLUE);

        p7.setMyShelf(new Shelf(testingShelf));
        assertTrue(testCard.checkPattern(p7));

    }
    @Test
    @DisplayName("CheckSharedGoal 12 test")
    public void checkSharedGoal12test(){
        int playerNumber = 2;
        Player p1 = new Player(fakeName);
        SharedGoal12Card testCard = new SharedGoal12Card(fakePath, playerNumber);
        Tile[][] testingShelf = new Tile[Settings.SHELFROW][Settings.SHELFCOLUMN];
        for(int i = 0; i< Settings.SHELFROW; i++){
            for(int j = 0; j< Settings.SHELFCOLUMN; j++){
                testingShelf[i][j] = new Tile(fakePath,Tile.Color.NULLTILE);
            }
        }
        p1.setMyShelf(new Shelf(testingShelf));
        assertFalse(testCard.checkPattern(p1));

        testingShelf[0][0] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[1][0] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[2][0] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[3][0] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[4][0] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[5][0] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[1][1] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[2][1] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[3][1] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[4][1] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[5][1] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[2][2] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[3][2] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[4][2] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[5][2] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[3][3] = new Tile(fakePath, Tile.Color.PINK);
        testingShelf[4][3] = new Tile(fakePath, Tile.Color.PINK);
        testingShelf[5][3] = new Tile(fakePath, Tile.Color.PINK);
        testingShelf[4][4] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[5][4] = new Tile(fakePath, Tile.Color.PINK);

        p1.setMyShelf(new Shelf(testingShelf));
        assertTrue(testCard.checkPattern(p1));
        assertFalse(testCard.checkPattern(p1)); // p1 has already achieved the goal

        Player p2 = new Player(fakeName);

        for(int i = 0; i< Settings.SHELFROW; i++){
            for(int j = 0; j< Settings.SHELFCOLUMN; j++){
                testingShelf[i][j] = new Tile(fakePath,Tile.Color.NULLTILE);
            }
        }

        testingShelf[5][0] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[5][1] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[5][2] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[5][3] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[5][4] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[4][1] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[4][2] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[4][3] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[4][4] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[3][2] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[3][3] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[3][4] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[2][3] = new Tile(fakePath, Tile.Color.PINK);
        testingShelf[2][4] = new Tile(fakePath, Tile.Color.PINK);
        testingShelf[1][4] = new Tile(fakePath, Tile.Color.PINK);

        p2.setMyShelf(new Shelf(testingShelf));
        assertTrue(testCard.checkPattern(p2));

        Player p3 = new Player(fakeName);

        for(int i = 0; i< Settings.SHELFROW; i++){
            for(int j = 0; j< Settings.SHELFCOLUMN; j++){
                testingShelf[i][j] = new Tile(fakePath,Tile.Color.NULLTILE);
            }
        }

        testingShelf[5][1] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[5][2] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[5][3] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[5][4] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[4][1] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[4][2] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[4][3] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[4][4] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[3][2] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[3][3] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[3][4] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[2][3] = new Tile(fakePath, Tile.Color.PINK);
        testingShelf[2][4] = new Tile(fakePath, Tile.Color.PINK);
        testingShelf[1][4] = new Tile(fakePath, Tile.Color.PINK);

        p3.setMyShelf(new Shelf(testingShelf));
        assertFalse(testCard.checkPattern(p3));

        Player p4 = new Player(fakeName);

        for(int i = 0; i< Settings.SHELFROW; i++){
            for(int j = 0; j< Settings.SHELFCOLUMN; j++){
                testingShelf[i][j] = new Tile(fakePath,Tile.Color.NULLTILE);
            }
        }

        testingShelf[5][0] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[5][1] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[5][2] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[5][3] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[5][4] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[4][1] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[4][2] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[4][3] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[4][4] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[3][2] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[3][3] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[3][4] = new Tile(fakePath, Tile.Color.BLUE);
        testingShelf[2][3] = new Tile(fakePath, Tile.Color.PINK);
        testingShelf[2][4] = new Tile(fakePath, Tile.Color.PINK);

        p4.setMyShelf(new Shelf(testingShelf));
        assertFalse(testCard.checkPattern(p4));
    }

    private void resetMatrix(Tile[][] matrix){
        for(int i = 0; i< Settings.SHELFROW; i++){
            for(int j = 0; j< Settings.SHELFCOLUMN; j++){
                matrix[i][j].setColor(Tile.Color.NULLTILE);
            }
        }
    }



    @Test
    @DisplayName("checkTokenStack 9 test")
    public void checkTokenStack() {
        int playerNumber2 = 2;
        int playerNumber3 = 3;
        int playerNumber4 = 4;
        Game game2 = new Game("shdgfhji", playerNumber2);
        Game game3 = new Game("shdgfhji", playerNumber3);
        Game game4 = new Game("shdgfhji", playerNumber4);

        System.out.println("Game 2:\nsg1:"+game2.getSharedDeck().get(0).popPointToken()+" "+game2.getSharedDeck().get(0).popPointToken());
        System.out.println("sg2: "+game2.getSharedDeck().get(1).popPointToken()+" "+game2.getSharedDeck().get(1).popPointToken());
        System.out.println("Game 3:\nsg1:"+game3.getSharedDeck().get(0).popPointToken()+" "+game3.getSharedDeck().get(0).popPointToken()+" "+game3.getSharedDeck().get(0).popPointToken());
        System.out.println("sg2: "+game3.getSharedDeck().get(1).popPointToken()+" "+game3.getSharedDeck().get(1).popPointToken()+" "+game3.getSharedDeck().get(1).popPointToken());
        System.out.println("Game 4:\nsg1:"+game4.getSharedDeck().get(0).popPointToken()+" "+game4.getSharedDeck().get(0).popPointToken()+" "+game4.getSharedDeck().get(0).popPointToken()+" "+game4.getSharedDeck().get(0).popPointToken());
        System.out.println("sg2: "+game4.getSharedDeck().get(1).popPointToken()+" "+game4.getSharedDeck().get(1).popPointToken()+" "+game4.getSharedDeck().get(1).popPointToken()+" "+game4.getSharedDeck().get(1).popPointToken());
     }
    }

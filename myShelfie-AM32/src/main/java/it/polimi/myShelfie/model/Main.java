package it.polimi.myShelfie.model;

import it.polimi.myShelfie.model.cards.*;
import it.polimi.myShelfie.utilities.Constants;

public class Main {
    public static void main(String[] args) {
        Game game2 = new Game(2);

        Player p1 = new Player("root","fakeAddress");
        game2.addPlayer(p1);

        /*System.out.println(game2.getGameBoard().toString()+"\n\n");
        System.out.println(game3.getGameBoard().toString()+"\n\n");
        System.out.println(game4.getGameBoard().toString());*/

        /*
         * Print whole deck patterns
         */

        /*
        for(int i=0;i<12;i++) {
            System.out.println(game2.drawPersonalGoal().toString());
        }
        System.out.println(game2.drawPersonalGoal().toString()); //TEST FOR OUTERBOUND INDEX EXCEPTION
         */

        SharedGoalCard testCard = new SharedGoal3Card("");
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

        p1.setMyShelf(new Shelf(testingShelfAchieved));

        if(testCard.checkPattern(p1)){
            System.out.println("Achieved");
        }
        else{
            System.out.println("Not achieved");
        }




    }
}

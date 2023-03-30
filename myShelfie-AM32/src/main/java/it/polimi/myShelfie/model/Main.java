package it.polimi.myShelfie.model;

import it.polimi.myShelfie.model.cards.CheckSharedGoal;
import it.polimi.myShelfie.model.cards.SharedGoal1Card;
import it.polimi.myShelfie.model.cards.SharedGoalCard;
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

        Tile[][] testingShelf = new Tile[Constants.SHELFROW][Constants.SHELFCOLUMN];
        for(int i=0;i<Constants.SHELFROW;i++){
            for(int j=0;j<Constants.SHELFCOLUMN;j++){
                testingShelf[i][j] = new Tile("",Tile.Color.NULLTILE);
            }
        }

        testingShelf[0][0].setColor(Tile.Color.GREEN);
        testingShelf[1][0].setColor(Tile.Color.LIGHTBLUE);
        testingShelf[2][0].setColor(Tile.Color.PINK);
        testingShelf[3][0].setColor(Tile.Color.WHITE);
        testingShelf[4][0].setColor(Tile.Color.YELLOW);
        testingShelf[5][0].setColor(Tile.Color.BLUE);

        testingShelf[0][3].setColor(Tile.Color.BLUE);
        testingShelf[1][3].setColor(Tile.Color.LIGHTBLUE);
        testingShelf[2][3].setColor(Tile.Color.WHITE);
        testingShelf[3][3].setColor(Tile.Color.GREEN);
        testingShelf[4][3].setColor(Tile.Color.PINK);
        testingShelf[5][3].setColor(Tile.Color.BLUE);

        p1.setMyShelf(new Shelf(testingShelf));

        if(game2.sharedDeck.get(0).checkPattern(p1)){
            System.out.println("Achieved");
        }
        else{
            System.out.println("Not achieved");
        }




    }
}

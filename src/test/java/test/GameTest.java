package test;

import it.polimi.myShelfie.model.Game;
import it.polimi.myShelfie.model.Player;
import it.polimi.myShelfie.model.Position;
import it.polimi.myShelfie.model.Tile;
import it.polimi.myShelfie.utilities.Utils;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GameTest {
    private final String fakeIP = "192.168.1.1";
    private final String fakeName = "testName";

    @Test
    @DisplayName("Turn handling test")
    public void turnHandlingTest(){
        Game myGame = new Game("");
        assertEquals(myGame.getCurrentPlayer(), 0);
        myGame.handleTurn();
        assertEquals(myGame.getCurrentPlayer(),1);
    }

    @Test
    @DisplayName("Pick tiles test")
    public void pickTilesTest(){
        Player p1 = new Player("pippo",fakeIP);
        Player p2 = new Player("topolino",fakeIP);

        Game game = new Game("fakeUID",2);
        game.addPlayer(p1);
        game.addPlayer(p2);

        System.out.println(game.getGameBoard().toString());

        String collectedTile = game.collectTile(new Position(2,3)).get(0).toString();
        assertEquals(collectedTile,"-");
    }

    @Test
    @DisplayName("Tile insert test")
    public void tileInsertTest(){
        Game myGame = new Game("",2);
        myGame.getPlayers().add(new Player(fakeName,fakeIP));
        List<Tile> tiles = new ArrayList<>();
        tiles.add(new Tile("", Tile.Color.BLUE));
        tiles.add(new Tile("", Tile.Color.GREEN));
        tiles.add(new Tile("", Tile.Color.LIGHTBLUE));
        myGame.insertTiles(tiles,1);
        System.out.println(myGame.getPlayers().get(myGame.getCurrentPlayer()).getMyShelf().toString());
    }

    @Test
    @DisplayName("Winner check test")
    public void winnerCheckTest(){
        Player p1 = new Player("luca", fakeIP);
        Player p2 = new Player("albe", fakeIP);
        Player p3 = new Player("matteo", fakeIP);
        p1.setScore(2);
        p2.setScore(6);
        p3.setScore(6);
        List<Player> myPlayers = new ArrayList<>();
        myPlayers.add(p1);
        myPlayers.add(p2);
        myPlayers.add(p3);
        Game myGame = new Game(Utils.UIDGenerator(),3);
        myGame.setPlayers(myPlayers);
        System.out.println(myGame.getRank());
    }

    @Test
    @DisplayName("Game saving test")
    public void gameSavingTest(){
        Player p1 = new Player("pippo",fakeIP);
        Player p2 = new Player("topolino",fakeIP);
        Player p3 = new Player("pluto", fakeIP);

        Game game = new Game("fakeUID",3);
        game.addPlayer(p1);
        game.addPlayer(p2);
        game.addPlayer(p3);

        System.out.println(game.getGameBoard().toString());

        p1.setGoalCard(game.drawPersonalGoal());
        p2.setGoalCard(game.drawPersonalGoal());
        p3.setGoalCard(game.drawPersonalGoal());

        p1.getMyShelf().insertTile(game.collectTile(new Position(0,3)).get(0),0);
        p1.getMyShelf().insertTile(game.collectTile(new Position(1,3)).get(0),0);

        p2.getMyShelf().insertTile(game.collectTile(new Position(2,2)).get(0),1);
        p2.getMyShelf().insertTile(game.collectTile(new Position(3,2)).get(0),0);

        p3.getMyShelf().insertTile(game.collectTile(new Position(5,0)).get(0),2);
        p3.getMyShelf().insertTile(game.collectTile(new Position(6,2)).get(0),2);

        p1.setScore(50);
        p2.setScore(5);
        p3.setScore(1);

        game.saveGame();

        assertTrue(true);
    }

    @Test
    @DisplayName("Game loading test")
    public void gameLoadingTest(){
        Player p1 = new Player("pippo",fakeIP);
        Player p2 = new Player("topolino",fakeIP);
        Player p3 = new Player("pluto", fakeIP);

        Game game = new Game("fakeUID",3);
        game.addPlayer(p1);
        game.addPlayer(p2);
        game.addPlayer(p3);


        p1.setGoalCard(game.drawPersonalGoal());
        p2.setGoalCard(game.drawPersonalGoal());
        p3.setGoalCard(game.drawPersonalGoal());

        p1.getMyShelf().insertTile(game.collectTile(new Position(0,3)).get(0),0);
        p1.getMyShelf().insertTile(game.collectTile(new Position(1,3)).get(0),0);

        p2.getMyShelf().insertTile(game.collectTile(new Position(2,2)).get(0),1);
        p2.getMyShelf().insertTile(game.collectTile(new Position(3,2)).get(0),0);

        p3.getMyShelf().insertTile(game.collectTile(new Position(5,0)).get(0),2);
        p3.getMyShelf().insertTile(game.collectTile(new Position(6,2)).get(0),2);


        p1.setScore(50);
        p2.setScore(5);
        p3.setScore(1);

        game.saveGame();


        Game game2 = new Game("fakeUID");


        assertEquals(game.getUID(),game2.getUID());
        assertEquals(game.getPlayers().size(),game2.getOldGamePlayers().size());
        assertEquals(game.getPlayersNumber(),game2.getPlayersNumber());
        for(int i=0;i<game.getPlayersNumber();i++){
            assertEquals(game.getPlayers().get(i).toString(),game2.getOldGamePlayers().get(i).toString());
            assertEquals(game.getPlayers().get(i).getMyShelf().toString(),game2.getOldGamePlayers().get(i).getMyShelf().toString());
            assertEquals(game.getPlayers().get(i).getMyGoalCard().toString(),game2.getOldGamePlayers().get(i).getMyGoalCard().toString());
        }
        assertEquals(game.getSharedDeck().size(),2);
        for(int i=0;i<game.getSharedDeck().size();i++){
            assertEquals(game.getSharedDeck().get(i).toString(), game2.getSharedDeck().get(i).toString());
        }
        assertEquals(game.getGameBoard().toString(),game2.getGameBoard().toString());
        assertEquals(game.getGameBoard().getTileHeapToString(),game2.getGameBoard().getTileHeapToString());
    }
}

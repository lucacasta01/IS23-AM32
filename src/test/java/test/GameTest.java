package test;

import it.polimi.myShelfie.application.Server;
import it.polimi.myShelfie.model.Game;
import it.polimi.myShelfie.model.Player;
import it.polimi.myShelfie.model.cards.PersonalGoalCard;
import it.polimi.myShelfie.model.cards.SharedGoalCard;
import it.polimi.myShelfie.utilities.Position;
import it.polimi.myShelfie.model.Tile;
import it.polimi.myShelfie.utilities.Settings;
import it.polimi.myShelfie.utilities.Utils;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import java.sql.ClientInfoStatus;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class GameTest {
    private final String fakeIP = "192.168.1.1";
    private final String fakeName = "testName";

    @Test
    @DisplayName("Turn handling test")
    public void turnHandlingTest(){
        Game myGame = new Game("",2);
        assertEquals(myGame.getCurrentPlayer(), 0);
        myGame.handleTurn();
        assertEquals(myGame.getCurrentPlayer(),1);
    }

    @Test
    @DisplayName("Pick tiles test")
    public void pickTilesTest(){
        Player p1 = new Player("pippo");
        Player p2 = new Player("topolino");

        Game game = new Game("fakeUID",2);
        game.addPlayer(p1);
        game.addPlayer(p2);

        game.getGameBoard().initBoard(2);
        System.out.println(game.getGameBoard().toString());

        String[] expectedTiles = new String[]{
                game.getGameBoard().getGrid()[1][3].toString(),
                game.getGameBoard().getGrid()[1][4].toString(),
                game.getGameBoard().getGrid()[0][2].toString()  // "-"
        };
        List<String> collectedTiles = new ArrayList<>();
        System.out.println("board");
        List<Tile> collected1 = game.collectTiles(List.of(new Position(1,3), new Position(1,4)));
        collected1.forEach((t->{
            collectedTiles.add(t.toString());
        }));

        List<Tile> tile3;
        List<Position> toCollect = new ArrayList<>();
        toCollect.add(new Position(0,2));
        if((tile3 = game.collectTiles(toCollect)) != null){
            collectedTiles.add(tile3.get(0).toString());
        }
        else{
            collectedTiles.add("-");
        }


        for(int i=0;i< collectedTiles.size();i++){
            assertEquals(collectedTiles.get(i),expectedTiles[i]);
        }
    }

    @Test
    @DisplayName("Tile insert test")
    public void tileInsertTest(){
        Game myGame = new Game("",2);
        myGame.getPlayers().add(new Player(fakeName));
        List<Tile> tiles = new ArrayList<>();
        tiles.add(new Tile("", Tile.Color.BLUE));
        tiles.add(new Tile("", Tile.Color.GREEN));
        tiles.add(new Tile("", Tile.Color.LIGHTBLUE));
        myGame.insertTiles(tiles,1);
        System.out.println(myGame.getPlayers().get(myGame.getCurrentPlayer()).getMyShelf().toString());
    }

    @Test
    @DisplayName("Add player test")
    public void addPlayerTest(){
        Game game2 = new Game("",2);
        Game game3 = new Game("",3);
        Game game4 = new Game("",4);
        Player p1 = new Player("f");
        Player p2 = new Player("s");
        Player p3 = new Player("t");
        Player p4 = new Player("g");
        Player p5 = new Player("e");
        game2.addPlayer(p1);
        game2.addPlayer(p2);
        game2.addPlayer(p3);
        System.out.println("Game 2 size: "+game2.getPlayers().size());
        assertTrue(game2.getPlayers().size()==2);
        game3.addPlayer(p1);
        game3.addPlayer(p2);
        game3.addPlayer(p3);
        game3.addPlayer(p4);
        System.out.println("Game 3 size: "+game3.getPlayers().size());
        assertTrue(game3.getPlayers().size()==3);
        game4.addPlayer(p1);
        game4.addPlayer(p2);
        game4.addPlayer(p3);
        game4.addPlayer(p4);
        game4.addPlayer(p5);
        System.out.println("Game 4 size: "+game4.getPlayers().size());
        assertTrue(game4.getPlayers().size()==4);

    }

    @Test
    @DisplayName("Winner check test")
    public void winnerCheckTest(){
        Player p1 = new Player("luca");
        Player p2 = new Player("albe");
        Player p3 = new Player("matteo");
        Server server = Server.getInstance();
        p1.setScore(2);
        p2.setScore(6);
        p3.setScore(6);
        List<Player> myPlayers = new ArrayList<>();
        myPlayers.add(p1);
        myPlayers.add(p2);
        myPlayers.add(p3);
        Game myGame = new Game(Utils.UIDGenerator(),3);
        myGame.setPlayers(myPlayers);
        System.out.println(myGame.getRank(false));
    }

    @Test
    @DisplayName("refill board")
    public void refillBoard(){
        Game myGame = new Game("T30YX9Dp",2);
        if(myGame.getGameBoard().needToRefill()){
            myGame.getGameBoard().initBoard(2);
        }
        System.out.println(myGame.getGameBoard().toString());
        //todo
    }

    @Test
    @DisplayName("Draw personal goal test")
    public void drawPersonalGoalTest(){
        Game myGame = new Game("fakeUid",2);
        PersonalGoalCard personalGoalCard = myGame.drawPersonalGoal();
        System.out.println(personalGoalCard.toString());
        assertNotEquals(personalGoalCard, null);
    }

    @Test
    @DisplayName("Collect tiles test")
    public void collectTilesTest(){
        Game myGame = new Game("fakeUid",2);
        List<Position> toCollect = new ArrayList<>();
        List<Tile> collected = new ArrayList<>();
        System.out.println(myGame.getGameBoard().toString());
        toCollect.add(new Position(4,1));
        collected = myGame.collectTiles(toCollect);
        assertNotEquals(collected, null);
        assertEquals(collected.size(), 1);
        collected.clear();
        toCollect.clear();
        System.out.println(myGame.getGameBoard().toString());
        toCollect.add(new Position(3,2));
        toCollect.add(new Position(4,2));
        collected=myGame.collectTiles(toCollect);
        assertNotEquals(collected, null);
        assertEquals(collected.size(), 2);
        toCollect.clear();
        collected.clear();
        System.out.println(myGame.getGameBoard().toString());
        toCollect.add(new Position(4,3));
        toCollect.add(new Position(3,3));
        toCollect.add(new Position(2,3));
        collected=myGame.collectTiles(toCollect);
        assertNotEquals(collected, null);
        assertEquals(collected.size(), 3);
        System.out.println(myGame.getGameBoard().toString());
        toCollect.add(new Position(7,7));
        collected = myGame.collectTiles(toCollect);
        assertEquals(collected, null);
    }

    @Test
    @DisplayName("Check is last turn")
    public void checkLastTurnCheck(){
        Player p = new Player("fakename");
        Game game = new Game("fakeUID", 2);
        assertFalse(game.isLastTurn());
        game.checkLastTurn(p);
        assertFalse(game.isLastTurn());
        for(int i=0; i< Settings.SHELFCOLUMN; i++){
            for (int j=0; j<Settings.SHELFROW; j++){
                p.getMyShelf().insertTile(new Tile("test", Tile.Color.YELLOW), i);
            }
        }
        game.checkLastTurn(p);
        assertTrue(game.isLastTurn());
    }

    @Test
    @DisplayName("Rank test")
    public void getRankTest(){
        Game game2 = new Game("fakeUID", 2);
        Game game3 = new Game("fakeUID", 3);
        Game game4 = new Game("fakeUID", 4);
        Player p1 = new Player("f");
        Player p2 = new Player("s");
        Player p3 = new Player("t");
        Player p4 = new Player("f");

        p1.setScore(5);
        p2.setScore(4);
        game2.addPlayer(p1);
        game2.addPlayer(p2);
        System.out.println(game2.getRank(false));
        p2.setScore(5);
        System.out.println(game2.getRank(false));
        p2.setScore(4);

        p3.setScore(6);
        game3.addPlayer(p1);
        game3.addPlayer(p2);
        game3.addPlayer(p3);
        System.out.println(game3.getRank(false));

        p4.setScore(7);
        game4.addPlayer(p1);
        game4.addPlayer(p2);
        game4.addPlayer(p3);
        game4.addPlayer(p4);
        System.out.println(game4.getRank(false));


    }

    @Test
    @DisplayName("Game saving test")
    public void gameSavingTest(){
        Player p1 = new Player("pippo");
        Player p2 = new Player("topolino");
        Player p3 = new Player("pluto");

        Game game = new Game("fakeUID",3);
        game.addPlayer(p1);
        game.addPlayer(p2);
        game.addPlayer(p3);

        System.out.println(game.getGameBoard().toString());

        p1.setGoalCard(game.drawPersonalGoal());
        p2.setGoalCard(game.drawPersonalGoal());
        p3.setGoalCard(game.drawPersonalGoal());
        List<Position> toCollect = new ArrayList<>();
        toCollect.add(new Position(0,3));
        p1.getMyShelf().insertTile(game.collectTiles(toCollect).get(0),0);
        toCollect.clear();
        toCollect.add(new Position(1,3));
        p1.getMyShelf().insertTile(game.collectTiles(toCollect).get(0),0);
        toCollect.clear();
        toCollect.add(new Position(2,2));
        p2.getMyShelf().insertTile(game.collectTiles(toCollect).get(0),1);
        toCollect.clear();
        toCollect.add(new Position(3,2));
        p2.getMyShelf().insertTile(game.collectTiles(toCollect).get(0),0);
        toCollect.clear();
        toCollect.add(new Position(5,0));
        p3.getMyShelf().insertTile(game.collectTiles(toCollect).get(0),2);
        toCollect.clear();
        toCollect.add(new Position(6,2));
        p3.getMyShelf().insertTile(game.collectTiles(toCollect).get(0),2);

        p1.setScore(50);
        p2.setScore(5);
        p3.setScore(1);

        game.saveGame();

        assertTrue(true);
    }

    @Test
    @DisplayName("Game loading test")
    public void gameLoadingTest(){
        Player p1 = new Player("pippo");
        Player p2 = new Player("topolino");
        Player p3 = new Player("pluto");
        List<Position> toCollect =  new ArrayList<>();
        Game game = new Game("fakeUID",3);
        game.addPlayer(p1);
        game.addPlayer(p2);
        game.addPlayer(p3);


        p1.setGoalCard(game.drawPersonalGoal());
        p2.setGoalCard(game.drawPersonalGoal());
        p3.setGoalCard(game.drawPersonalGoal());
        toCollect.add(new Position(0,3));
        p1.getMyShelf().insertTile(game.collectTiles(toCollect).get(0),0);
        toCollect.clear();
        toCollect.add(new Position(1,3));
        p1.getMyShelf().insertTile(game.collectTiles(toCollect).get(0),0);
        toCollect.clear();
        toCollect.add(new Position(2,2));
        p2.getMyShelf().insertTile(game.collectTiles(toCollect).get(0),1);
        toCollect.clear();
        toCollect.add(new Position(3,2));
        p2.getMyShelf().insertTile(game.collectTiles(toCollect).get(0),0);
        toCollect.clear();
        toCollect.add(new Position(5,0));
        p3.getMyShelf().insertTile(game.collectTiles(toCollect).get(0),2);
        toCollect.clear();
        toCollect.add(new Position(6,2));
        p3.getMyShelf().insertTile(game.collectTiles(toCollect).get(0),2);


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

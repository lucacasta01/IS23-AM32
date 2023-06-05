package test.controller;
import static org.junit.jupiter.api.Assertions.*;

import it.polimi.myShelfie.controller.ClientHandler;
import it.polimi.myShelfie.controller.Lobby;
import it.polimi.myShelfie.controller.LobbyController;
import it.polimi.myShelfie.model.*;
import it.polimi.myShelfie.utilities.ANSI;
import it.polimi.myShelfie.utilities.Position;
import it.polimi.myShelfie.utilities.Settings;
import it.polimi.myShelfie.utilities.pojo.Action;
import it.polimi.myShelfie.utilities.pojo.View;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class LobbyControllerTest {
    @Test
    @DisplayName("pick tiles test")
    public void pickTilesTest(){
        Game game = new Game("fakeUID",2);
        Player p1 = new Player("p1");
        Player p2 = new Player("p2");
        LobbyController lobbyController = new LobbyController();
        lobbyController.setGame(game);
        lobbyController.addPlayer(List.of(p1,p2));

        Position pos1 = new Position(4,1);
        Position pos2 = new Position(5,1);

        Action pickAction1 = new Action(
                Action.ActionType.PICKTILES,
                p1.getUsername(),
                null,
                null,
                List.of(pos1,pos2),
                null
                );
        Action pickAction2 = new Action(
                Action.ActionType.PICKTILES,
                p2.getUsername(),
                null,
                null,
                List.of(pos1,pos2),
                null
        );

        String t1 = game.getGameBoard().getGrid()[pos1.getRow()][pos1.getColumn()].toString();
        String t2 = game.getGameBoard().getGrid()[pos2.getRow()][pos2.getColumn()].toString();

        String pickedTiles2 = lobbyController.pickTiles(pickAction2); //p1 turn => "0" expected
        String pickedTiles1 = lobbyController.pickTiles(pickAction1);

        assertEquals("0", pickedTiles2);
        assertTrue(pickedTiles1.contains(t1) && pickedTiles1.contains(t2));
    }
    @Test
    @DisplayName("select column")
    public void selectColumnTest(){
        Game game = new Game("fakeUID",2);
        Player p1 = new Player("p1");
        Player p2 = new Player("p2");
        LobbyController lobbyController = new LobbyController();
        lobbyController.setGame(game);
        lobbyController.addPlayer(List.of(p1,p2));

        //populating p1 shelf manually
        //first column is full, second column has 2 free spots, other column are empty
        Tile[][] matrix = new Tile[Settings.SHELFROW][Settings.SHELFCOLUMN];
        for(int j=0;j< Settings.SHELFCOLUMN;j++){
            for(int i=Settings.SHELFROW-1;i>=0;i--){
                if(j==0){
                    matrix[i][j] = new Tile("", Tile.Color.BLUE);
                }
                else if(j==1 && i>2){
                    matrix[i][j] = new Tile("", Tile.Color.LIGHTBLUE);
                }
                else{
                    matrix[i][j] = new Tile("", Tile.Color.NULLTILE);
                }
            }
        }
        p1.setMyShelf(new Shelf(matrix));
        String colorInserted = game.getGameBoard().getGrid()[4][1].toString();
        lobbyController.getCollectedTiles().add(game.getGameBoard().getGrid()[4][1]);

        //a1: p1 tries to insert 1 tile in col 1 -> expected "1"
        Action a1 = new Action(Action.ActionType.SELECTCOLUMN,p1.getUsername(),null,null,null,0);

        //a2: p2 tries to insert 1 tile in col 2 -> expected "0" (not his turn)
        Action a2 = new Action(Action.ActionType.SELECTCOLUMN,p2.getUsername(),null,null,null,1);

        //a3: p1 tries to insert 1 tile in col 2 -> expected "Tiles inserted correctly" and tile effectively in column 2
        Action a3 = new Action(Action.ActionType.SELECTCOLUMN,p1.getUsername(),null,null,null,1);


        assertEquals("1",lobbyController.selectColumn(a1));
        assertEquals("0",lobbyController.selectColumn(a2));
        assertEquals("Tiles inserted correctly",lobbyController.selectColumn(a3));
        assertEquals(colorInserted,p1.getMyShelf().getTileMartrix()[2][1].toString());
        assertNotEquals("-", p1.getMyShelf().getTileMartrix()[2][1].toString());
    }
    @Test
    @DisplayName("order tiles")
    public void orderTilesTest(){
        Game game = new Game("fakeUID",2);
        Player p1 = new Player("p1");
        Player p2 = new Player("p2");
        LobbyController lobbyController = new LobbyController();
        lobbyController.setGame(game);
        lobbyController.addPlayer(List.of(p1,p2));

        //collectedTiles.size() == 0
        assertEquals("1", lobbyController.orderTiles(new Action(
                Action.ActionType.ORDER,
                p1.getUsername(),
                null,
                "L B",
                null,
                null))
        );

        String[] collectedTiles = lobbyController.pickTiles(new Action(
                Action.ActionType.PICKTILES,
                p1.getUsername(),
                null,
                null,
                List.of(new Position(4,1),new Position(5,1)),
                null)
        ).substring("picked tiles: ".length()).split(" ");

        //not p2 turn
        assertEquals("0", lobbyController.orderTiles(new Action(
                Action.ActionType.ORDER,
                p2.getUsername(),
                null,
                collectedTiles[1]+" "+collectedTiles[0],
                null,
                null))
        );

        String[] newOrder = lobbyController.orderTiles(new Action(
                Action.ActionType.ORDER,
                p1.getUsername(),
                null,
                collectedTiles[1]+" "+collectedTiles[0],
                null,
                null)
        ).substring("Order changed successfully: ".length()).split(" ");

        assertTrue(collectedTiles[0].equals(newOrder[1]) && collectedTiles[1].equals(newOrder[0]));

    }
    @Test
    @DisplayName("reorder lobby players test")
    public void reorderLobbyPlayersTest(){
        Game game = new Game("fakeUid", 3);
        game.initializeoldGamePlayers();
        List<Player> oldGamePlayers = game.getOldGamePlayers();
        oldGamePlayers.add(new Player("1"));
        oldGamePlayers.add(new Player("2"));
        oldGamePlayers.add(new Player("3"));
        List<ClientHandler> lobbyPlayers = new ArrayList<>();
        ClientHandler ch1 = new ClientHandler(new Socket());
        ch1.setNickname("3");
        lobbyPlayers.add(ch1);
        ClientHandler ch2 = new ClientHandler(new Socket());
        ch2.setNickname("1");
        lobbyPlayers.add(ch2);
        ClientHandler ch3 = new ClientHandler(new Socket());
        ch3.setNickname("2");
        lobbyPlayers.add(ch3);
        LobbyController lobbyController = new LobbyController();
        lobbyController.setGame(game);
        ClientHandler[] orderedList = lobbyController.reorderLobbyPlayers(lobbyPlayers);
        assertEquals("1", orderedList[0].getNickname());
        assertEquals("2", orderedList[1].getNickname());
        assertEquals("3", orderedList[2].getNickname());

    }
    @Test
    @DisplayName("generate view test")
    public void generateViewTest(){
        Game game = new Game("fakeUID",2);
        Player p1 = new Player("p1");
        Player p2 = new Player("p2");
        LobbyController lobbyController = new LobbyController();
        lobbyController.setGame(game);
        lobbyController.addPlayer(List.of(p1,p2));

        ClientHandler chp1 = new ClientHandler();
        chp1.setNickname(p1.getUsername());
        chp1.setColor(ANSI.BLUE);

        ClientHandler chp2 = new ClientHandler();
        chp2.setNickname(p2.getUsername());
        chp2.setColor(ANSI.PURPLE);

        Lobby lobby = new Lobby(chp1,"fakeUID",2);
        lobby.getLobbyPlayers().add(chp2);
        View viewp1 = lobbyController.generateView(chp1,lobby);
        View viewp2 = lobbyController.generateView(chp2,lobby);

        //board
        assertEquals(game.getGameBoard().toString(), viewp1.getBoard());

        //shelves and score
        assertEquals(chp1.getColor()+p1.getUsername()+ANSI.RESET_COLOR+": "+p1.getScore()+" points\n"+p1.getMyShelf().toString(),viewp1.getShelves().get(0));
        assertEquals(chp2.getColor()+p2.getUsername()+ANSI.RESET_COLOR+": "+p2.getScore()+" points\n"+p2.getMyShelf().toString(),viewp2.getShelves().get(1));

        //shared cards
        for(int i=0;i<game.getSharedDeck().size();i++){
            assertEquals(game.getSharedDeck().get(i).toString(),viewp1.getSharedCards().get(i));
            assertEquals(game.getSharedDeck().get(i).toString(),viewp2.getSharedCards().get(i));
        }

        //personal cards
        assertEquals(p1.getMyGoalCard().toString(),viewp1.getPersonalCard());
        assertEquals(p2.getMyGoalCard().toString(),viewp2.getPersonalCard());

        //current player
        assertEquals(game.getPlayers().get(game.getCurrentPlayer()).getUsername(),viewp1.getCurrentPlayer());
        assertEquals(game.getPlayers().get(game.getCurrentPlayer()).getUsername(),viewp2.getCurrentPlayer());

        //players
        for(int i=0;i<game.getPlayers().size();i++){
            assertEquals(game.getPlayers().get(i).getUsername(),viewp1.getPlayers().get(i));
            assertEquals(game.getPlayers().get(i).getUsername(),viewp2.getPlayers().get(i));
        }

        //GUI shelves
        int k=0;
        for(int i=0;i<Settings.SHELFROW;i++){
            for(int j=0;j<Settings.SHELFCOLUMN;j++){
                assertEquals(p1.getMyShelf().getTileMartrix()[i][j].getImagePath(),viewp1.getCurPlayerGUIShelf().get(k));
                assertEquals(p2.getMyShelf().getTileMartrix()[i][j].getImagePath(),viewp1.getOthersGUIShelves().get(0).get(k));
                k++;
            }
        }

        //GUI scores
        assertEquals(p1.getScore(),viewp1.getGUIScoring().get(0));
        assertEquals(p2.getScore(),viewp1.getGUIScoring().get(1));

        //GUI board
        k=0;
        for(int i=0;i<Settings.BOARD_DIM;i++){
            for(int j=0;j<Settings.BOARD_DIM;j++){
                assertEquals(game.getGameBoard().getGrid()[i][j].getImagePath(),viewp1.getGUIboard().get(k));
                k++;
            }
        }

        //GUI shared cards
        assertEquals(game.getSharedDeck().get(0).getImgPath(),viewp1.getGUIsharedCards().get(0));
        assertEquals(game.getSharedDeck().get(1).getImgPath(),viewp1.getGUIsharedCards().get(1));

        //GUI personal cards
        assertEquals(p1.getMyGoalCard().getImgPath(),viewp1.getGUIpersonalCard());
        assertEquals(p2.getMyGoalCard().getImgPath(),viewp2.getGUIpersonalCard());

        //ansi colors
        assertEquals(chp1.getColor(),viewp1.getANSIcolor());
        assertEquals(chp2.getColor(),viewp2.getANSIcolor());


    }
    @Test
    @DisplayName("end game checks test")
    public void endGameChecksTest(){
        Game game = new Game("FakeUid", 2);
        LobbyController lobbyController = new LobbyController();
        lobbyController.setGame(game);
        Player p1 = new Player("p1");
        Player p2 = new Player("p2");
        p1.setGoalCard(game.drawPersonalGoal());
        p2.setGoalCard(game.drawPersonalGoal());
        assertEquals(0, p1.getScore());
        assertEquals(0, p2.getScore());
        List<Player> players = new ArrayList<>();
        players.add(p1);
        players.add(p2);
        lobbyController.setGamePlayers(players);
        lobbyController.endGameChecks();
        assertEquals(0, p1.getScore());
        assertEquals(0, p2.getScore());

    }
    @Test
    @DisplayName("end turn checks test")
    public void endTurnChecksTest(){
        Game game = new Game("FakeUid", 2);
        LobbyController lobbyController = new LobbyController();
        lobbyController.setGame(game);
        Player p1 = new Player("p1");
        Player p2 = new Player("p2");
        assertEquals(0, p1.getScore());
        assertEquals(0, p2.getScore());
        List<Player> players = new ArrayList<>();
        players.add(p1);
        players.add(p2);
        p1.setGoalCard(game.drawPersonalGoal());
        p2.setGoalCard(game.drawPersonalGoal());
        lobbyController.setGamePlayers(players);
        lobbyController.endTurnChecks(p1.getUsername());
        assertEquals(0, p1.getScore());
        assertEquals(0, p2.getScore());
        lobbyController.handleTurn();
        lobbyController.endTurnChecks(p2.getUsername());
        assertEquals(0, p1.getScore());
        assertEquals(0, p2.getScore());

    }
    @Test
    @DisplayName("handle turn test")
    public void handleTurnTest(){
        Game game = new Game("FakeUid", 2);
        LobbyController lobbyController = new LobbyController();
        lobbyController.setGame(game);
        Player p1 = new Player("p1");
        Player p2 = new Player("p2");
        List<Player> players = new ArrayList<>();
        players.add(p1);
        players.add(p2);
        lobbyController.setGamePlayers(players);
        assertEquals("p1", lobbyController.getGamePlayers().get(game.getCurrentPlayer()).getUsername());
        game.handleTurn();
        assertEquals("p2", lobbyController.getGamePlayers().get(game.getCurrentPlayer()).getUsername());
        game.handleTurn();
        assertEquals("p1", lobbyController.getGamePlayers().get(game.getCurrentPlayer()).getUsername());

    }

}

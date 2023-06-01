package test.controller;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

import it.polimi.myShelfie.controller.ClientHandler;
import it.polimi.myShelfie.controller.LobbyController;
import it.polimi.myShelfie.model.Game;
import it.polimi.myShelfie.model.Player;
import it.polimi.myShelfie.model.Tile;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;
import java.util.List;

public class LobbyControllerTest {
    @Test
    @DisplayName("pick tiles test")
    public void pickTilesTest(){

    }
    @Test
    @DisplayName("select column")
    public void selectColumnTest(){

    }
    @Test
    @DisplayName("order tiles")
    public void orderTilesTest(){

    }
    @Test
    @DisplayName("reorder lobby players test")
    public void reorderLobbyPlayersTest(){

    }
    @Test
    @DisplayName("generate view test")
    public void generateViewTest(){

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

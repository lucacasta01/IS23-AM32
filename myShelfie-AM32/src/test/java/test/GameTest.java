package test;

import it.polimi.myShelfie.model.Game;
import it.polimi.myShelfie.model.Player;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GameTest {
    private final String fakeIP = "192.168.1.1";
    private final String fakeName = "testName";

    @Test
    @DisplayName("Turn handling test")
    public void turnHandlingTest(){
        Game myGame = new Game(5);
        assertEquals(myGame.getCurrentPlayer(), 0);
        myGame.handleTurn();
        assertEquals(myGame.getCurrentPlayer(),1);
    }

    @Test
    @DisplayName("Winner check test")
    public void winnerCheckTest(){
        Player p1 = new Player(fakeName, fakeIP);
        Player p2 = new Player(fakeName, fakeIP);
        Player p3 = new Player(fakeName, fakeIP);
        p1.setScore(2);
        p2.setScore(3);
        p3.setScore(4);
        List<Player> myPlayers = new ArrayList<>();
        myPlayers.add(p1);
        myPlayers.add(p2);
        myPlayers.add(p3);
        Game myGame = new Game(3);
        myGame.setPlayers(myPlayers);
        myGame.getWinner();
        assertEquals(myGame.getWinner().getUsername(), p3.getUsername());
    }


}

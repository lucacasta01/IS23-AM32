package test;

import it.polimi.myShelfie.model.Game;
import it.polimi.myShelfie.model.Player;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GameTest {

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
        Player p1 = new Player("p1", "192.168.0.1");
        Player p2 = new Player("p2", "192.168.0.2");
        Player p3 = new Player("p3", "192.168.0.3");
        p1.setScore(2);
        p2.setScore(3);
        p3.setScore(4);
        Player[] myPlayers = new Player[3];
        myPlayers[0] = p1;
        myPlayers[1] = p2;
        myPlayers[2] = p3;
        Game myGame = new Game(3);
        myGame.setPlayers(myPlayers);
        myGame.getWinner();
        assertEquals(myGame.getWinner().getUsername(), p3.getUsername());
    }


}

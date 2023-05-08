package test;

import it.polimi.myShelfie.model.Player;
import it.polimi.myShelfie.model.Shelf;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlayerTest {

    @Test
    @DisplayName("setScore test")
    public void setScore(){
        Player testPlayer1 = new Player("testPlayer1" );
        testPlayer1.setScore(10);
        assertEquals(10,testPlayer1.getScore());
        Player testPlayer2 = new Player("testPayer2" );
        testPlayer1.setScore(-5);
        assertEquals(-5,testPlayer1.getScore());
    }

    @Test
    @DisplayName("getScore test")
    public void getScore(){
        Player testPlayer3 = new Player("testPlayer3" );

    }

    @Test
    @DisplayName("getIpAddress test")
    public void getIpAddress(){
        Player testPlayer5;
        Player testPlayer4 = new Player("testPlayer4" );
        //assertEquals("128.0.0.1", testPlayer4.getIpAddress());
        //Player testPlayer5 = new Player("testPlayer5", );
        //assertEquals("128.1.1.1", testPlayer5.getIpAddress());
    }

    @Test
    @DisplayName("getUsername test")
    public void getUsername(){
        Player testPlayer6 = new Player("testPlayer6" );
        assertEquals("testPlayer6", testPlayer6.getUsername());
    }

    @Test
    @DisplayName("getMyShelf test")
    public void getMyShelf(){
        Player testPlayer7 = new Player("testPlayer7");
        Shelf Shelf7 = testPlayer7.getMyShelf();
        assertSame(Shelf7 , testPlayer7.getMyShelf() );
    }

    @Test
    @DisplayName("getFailedPings test")
    public void getFailedPings(){

    }

    @Test
    @DisplayName("addFailedPings test")
    public void addFailedPings(){

    }


}

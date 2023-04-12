package test;

import it.polimi.myShelfie.model.Tile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;


public class TileTest {
    private final String fakePath = "testPath";
    @Test
    @DisplayName("setColor method")
    void setColor(){
        Tile testTile1 = new Tile(fakePath, Tile.Color.BLUE);
        testTile1.setColor(Tile.Color.GREEN);
        assertEquals(Tile.Color.GREEN, testTile1.getColor());
        Tile testTile2 = new Tile(fakePath, Tile.Color.NULLTILE);
        testTile2.setColor(Tile.Color.PINK);
        assertEquals(Tile.Color.PINK, testTile2.getColor());
    }
    @Test
    @DisplayName("getColor method")
    void getColor(){
        Tile testTile3 = new Tile(fakePath, Tile.Color.YELLOW);
        assertEquals(Tile.Color.YELLOW, testTile3.getColor());
        Tile testTile4 = new Tile(fakePath, Tile.Color.WHITE);
        assertEquals(Tile.Color.WHITE, testTile4.getColor());
        Tile testTile5 = new Tile(fakePath, Tile.Color.LIGHTBLUE);
        assertEquals(Tile.Color.LIGHTBLUE, testTile5.getColor());


    }

}

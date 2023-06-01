package test.model;
import static org.junit.jupiter.api.Assertions.*;

import it.polimi.myShelfie.model.Shelf;
import it.polimi.myShelfie.model.Tile;
import org.junit.jupiter.api.DisplayName;
import org.junit.Test;

public class ShelfTest {
    private final int MAXROW = 6;
    private final int MAXCOLUMN = 5;
    @Test
    @DisplayName("initShelf test")
    public void initShelfTest(){
        Shelf testShelf = new Shelf();
        Tile[][] matrix;
        testShelf.initShelf();
        matrix=testShelf.getTileMartrix();
        for(int i=0; i<MAXROW; i++){
            for(int j=0; j<MAXCOLUMN; j++){
                assertEquals(Tile.Color.NULLTILE, matrix[i][j].getColor());
            }
        }
    }
    @Test
    @DisplayName("freeTiles test")
    public void freeTilesTest(){

        Shelf testShelf = new Shelf();
        testShelf.initShelf();
       for(int i=0; i<MAXROW; i++){
           testShelf.insertTile(new Tile("test", Tile.Color.BLUE), 0);
       }
        assertEquals(0, testShelf.freeTiles(0));
        assertEquals(6, testShelf.freeTiles(1));
    }

    @Test
    @DisplayName("Check is full test")
    public void checkIsFullTest(){

        Shelf testShelf = new Shelf();
        testShelf.initShelf();
        assertFalse(testShelf.checkIsFull());
        for(int j=0; j<MAXCOLUMN; j++) {
            for (int i = 0; i < MAXROW; i++) {
                testShelf.insertTile(new Tile("test", Tile.Color.BLUE), j);
            }
        }
        assertTrue(testShelf.checkIsFull());
    }

    @Test
    @DisplayName("insertTiles test")
    public void insertTilesTest(){

        Shelf testShelf = new Shelf();
        testShelf.initShelf();
        for(int i=0; i<MAXROW; i++){
            testShelf.insertTile(new Tile("test", Tile.Color.BLUE), 0);
        }
        System.out.println(testShelf.toString());
        testShelf.insertTile(new Tile("test", Tile.Color.BLUE), 0);
    }


    @Test
    @DisplayName("getFinalPoints test")
    public void getFinalPointsTest(){
        Shelf testShelf = new Shelf();
        testShelf.initShelf();
        for(int i=0; i<MAXROW; i++){
            testShelf.insertTile(new Tile("test", Tile.Color.BLUE), 0);
        }
        for(int i=0; i<MAXROW; i++){
            testShelf.insertTile(new Tile("test", Tile.Color.GREEN), 1);
        }
        for(int i=0; i<MAXROW; i++){
            testShelf.insertTile(new Tile("test", Tile.Color.LIGHTBLUE), 2);
        }
        for(int i=0; i<MAXROW; i++){
            testShelf.insertTile(new Tile("test", Tile.Color.PINK), 3);
        }
        for(int i=0; i<MAXROW; i++){
            testShelf.insertTile(new Tile("test", Tile.Color.WHITE), 4);
        }
        System.out.println(testShelf.toString());
        assertEquals(40, testShelf.getShelfScore());
    }
}


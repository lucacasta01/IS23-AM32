package test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
import it.polimi.myShelfie.model.Player;
import it.polimi.myShelfie.model.Position;
import it.polimi.myShelfie.model.Shelf;
import it.polimi.myShelfie.model.Tile;
import it.polimi.myShelfie.model.cards.PersonalGoalCard;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ShelfTest {
    private final int MAXROW = 6;
    private final int MAXCOLUMN = 5;
    @Test
    @DisplayName("initShelf test")
    void initShelfTest(){
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
    void freeTilesTest(){

        Shelf testShelf = new Shelf();
        testShelf.initShelf();
       for(int i=0; i<MAXROW; i++){
           testShelf.insertTile(new Tile("test", Tile.Color.BLUE), 0);
       }
        assertEquals(0, testShelf.freeTiles(0));
        assertEquals(6, testShelf.freeTiles(1));
    }

    @Test
    @DisplayName("insertTiles test")
    void insertTilesTest(){

        Shelf testShelf = new Shelf();
        testShelf.initShelf();
        for(int i=0; i<MAXROW; i++){
            testShelf.insertTile(new Tile("test", Tile.Color.BLUE), 0);
        }
        System.out.println(testShelf.toString());
    }
}


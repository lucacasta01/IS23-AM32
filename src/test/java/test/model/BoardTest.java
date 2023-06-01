package test.model;
import static org.junit.jupiter.api.Assertions.*;

import it.polimi.myShelfie.model.Board;
import it.polimi.myShelfie.model.Tile;
import it.polimi.myShelfie.utilities.Settings;
import org.junit.jupiter.api.DisplayName;
import org.junit.Test;
public class BoardTest {
    @Test
    @DisplayName("needToRefill test")
    public void needToRefillTest(){
        Board boardToRefill = new Board();
        Board boardNotToRefill = new Board();
        for(int i = 0; i< Settings.BOARD_DIM; i++){
            for(int j = 0; j< Settings.BOARD_DIM; j++){
                boardToRefill.getGrid()[i][j] = new Tile("fakePath", Tile.Color.NULLTILE);
                boardNotToRefill.getGrid()[i][j] = new Tile("fakePath", Tile.Color.NULLTILE);
            }
        }

        boardToRefill.getGrid()[3][3].setColor(Tile.Color.BLUE);
        boardToRefill.getGrid()[4][4].setColor(Tile.Color.BLUE);
        boardToRefill.getGrid()[6][3].setColor(Tile.Color.BLUE);
        boardToRefill.getGrid()[7][1].setColor(Tile.Color.BLUE);

        boardNotToRefill.getGrid()[3][3].setColor(Tile.Color.BLUE);
        boardNotToRefill.getGrid()[3][4].setColor(Tile.Color.LIGHTBLUE);
        boardNotToRefill.getGrid()[1][3].setColor(Tile.Color.BLUE);
        boardNotToRefill.getGrid()[1][5].setColor(Tile.Color.LIGHTBLUE);

        System.out.println(boardToRefill.toString());
        System.out.println(boardNotToRefill.toString());

        assertTrue(boardToRefill.needToRefill());
        assertFalse(boardNotToRefill.needToRefill());
    }

    @Test
    @DisplayName("isCatchable test")
    public void isCatchableTest() {
        Board board =new Board();
        board.initBoard(2);
        System.out.println(board.toString());
        assertTrue(board.isCatchable(3,2));
        assertTrue(board.isCatchable(6,5));
        assertFalse(board.isCatchable(4,4));
        assertFalse(board.isCatchable(7,7));
    }

    @Test
    @DisplayName("Init board test")
    public void initBoardTest() {
        Board board2p =new Board();
        Board board3p=new Board();
        Board board4p = new Board();
        board2p.initBoard(2);
        board3p.initBoard(3);
        board4p.initBoard(4);
        System.out.println("2 players board: \n"+board2p.toString()+"\n");
        System.out.println("3 players board: \n"+board3p.toString()+"\n");
        System.out.println("4 players board: \n"+board4p.toString());

    }
}

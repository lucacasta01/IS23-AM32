package test;

import com.sun.javafx.font.CompositeStrike;
import it.polimi.myShelfie.utilities.ANSI;
import it.polimi.myShelfie.utilities.Position;
import it.polimi.myShelfie.utilities.Utils;
import javafx.geometry.Pos;
import org.junit.jupiter.api.DisplayName;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UtilsTest {
    @Test
    @DisplayName("UID generator test")
    public void UIDGeneratorTest() {
        for(int i=0;i<100;i++){
            assertTrue(Utils.UIDGenerator().matches("^[a-zA-Z0-9]+$")); //UID contains only alphanumeric char
        }
    }

    @Test
    @DisplayName("Substrings test")
    public void substringsTest() {
        String s = "/collect 12,31,11";
        int firstTile = "/collect ".length();
        String substr = s.substring(firstTile);
        String[] pos = substr.split(",");
        assertEquals("12",pos[0]);
        assertEquals("31",pos[1]);
        assertEquals("11",pos[2]);
    }

    @Test
    @DisplayName("Nickname format checker test")
    public void nicknameFormatTest() {
        assertTrue(Utils.checkNicknameFormat("valid_nickname"));
        assertFalse(Utils.checkNicknameFormat("invalid nickname"));
        assertFalse(Utils.checkNicknameFormat("\n"));
        assertFalse(Utils.checkNicknameFormat("/"));
    }

    @Test
    @DisplayName("Adjacent test")
    public void adjacentTest(){
        Position p1 = new Position(2,3);
        Position p2 = new Position(2,4);
        Position p3 = new Position(2,5);
        Position p4 = new Position(5,3);
        Position p5 = new Position(3,3);
        Position p6 = new Position(4,3);

        List<Position> adj = List.of(p1,p2,p3);
        List<Position> notAdj = List.of(p1,p3);

        assertTrue(Position.areAdjacent(adj));
        assertFalse(Position.areAdjacent(notAdj));

        adj = List.of(p5,p6,p1);
        notAdj = List.of(p1,p5, p4);

        assertTrue(Position.areAdjacent(adj));
        assertFalse(Position.areAdjacent(notAdj));
    }
}

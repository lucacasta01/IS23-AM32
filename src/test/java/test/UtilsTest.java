package test;

import it.polimi.myShelfie.utilities.ANSI;
import it.polimi.myShelfie.utilities.Utils;
import org.junit.jupiter.api.DisplayName;
import org.junit.Test;

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
}

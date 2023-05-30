package test;

import it.polimi.myShelfie.utilities.Utils;
import org.junit.jupiter.api.DisplayName;
import org.junit.Test;
public class UtilsTest {
    @Test
    @DisplayName("UID generator test")
    public void UIDGeneratorTest() {
        for(int i=0;i<100;i++){
            String UID = Utils.UIDGenerator();
            System.out.println(UID);
        }
    }

    @Test
    @DisplayName("Substrings test")
    public void substringsTest() {
        String s = "/collect 12,31,11";
        int firstTile = s.indexOf("/collect") + "/collect ".length();
        String substr = s.substring(firstTile);
        String[] pos = substr.split(",");
        for(String str : pos){
            System.out.println(str);
        }

    }
}

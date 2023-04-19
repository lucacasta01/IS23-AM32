package test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

import it.polimi.myShelfie.model.Board;
import it.polimi.myShelfie.model.Position;
import it.polimi.myShelfie.model.Tile;
import it.polimi.myShelfie.model.cards.PersonalGoalCard;
import it.polimi.myShelfie.server.Server;
import it.polimi.myShelfie.utilities.Constants;
import it.polimi.myShelfie.utilities.Utils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
public class UtilsTest {
    @Test
    @DisplayName("UID generator test")
    void UIDGeneratorTest() {
        System.out.println(Utils.UIDGenerator());
    }
}

package test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
import it.polimi.myShelfie.model.Player;
import it.polimi.myShelfie.model.Position;
import it.polimi.myShelfie.model.Tile;
import it.polimi.myShelfie.model.cards.PersonalGoalCard;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;



public class PersonalGoalCardTest {
    @Test
    @DisplayName("setPattern test")
    void setPatternTest() {
        PersonalGoalCard testCard = new PersonalGoalCard("test");
        Tile[][] pattern = new Tile[6][5];
        List<Tile.Color> colors = new ArrayList<>();
        List<Position> positions = new ArrayList<>();
        colors.add(Tile.Color.BLUE);
        colors.add(Tile.Color.WHITE);
        positions.add(new Position(0, 0));
        positions.add(new Position(2, 2));
        testCard.setPattern(positions, colors);
        pattern = testCard.getPattern();
        assertEquals(pattern[0][0].getColor(), Tile.Color.BLUE);
        assertEquals(pattern[2][2].getColor(), Tile.Color.WHITE);
    }
}

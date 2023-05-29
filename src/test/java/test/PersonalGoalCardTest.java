package test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

import it.polimi.myShelfie.utilities.ColorPosition;
import it.polimi.myShelfie.utilities.Position;

import it.polimi.myShelfie.model.Player;
import it.polimi.myShelfie.model.*;
import it.polimi.myShelfie.model.Tile;
import it.polimi.myShelfie.model.cards.PersonalGoalCard;
import it.polimi.myShelfie.utilities.JsonParser;
import it.polimi.myShelfie.utilities.Settings;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;



public class PersonalGoalCardTest {

    private final String fakePath = "testPath";

    @Test
    @DisplayName("setPattern test")
    void setPatternTest() {
        PersonalGoalCard testCard = new PersonalGoalCard(fakePath);
        Tile[][] pattern;
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

    @Test
    @DisplayName("checkPattern test")
    void checkPatternTest(){
        PersonalGoalCard testCard = new PersonalGoalCard(fakePath);
        List<Position> positions = new ArrayList<>();
        List<Tile.Color> colors = new ArrayList<>();
        colors.add(Tile.Color.PINK);
        colors.add(Tile.Color.BLUE);
        colors.add(Tile.Color.GREEN);
        colors.add(Tile.Color.WHITE);
        colors.add(Tile.Color.YELLOW);
        colors.add(Tile.Color.LIGHTBLUE);
        positions.add(new Position(0,0));
        positions.add(new Position(0,2));
        positions.add(new Position(1,4));
        positions.add(new Position(2,3));
        positions.add(new Position(3,1));
        positions.add(new Position(5,2));
        testCard.setPattern(positions, colors);
        Player testPlayer = new Player("fakeName");
        Tile[][] matrix = new Tile[Settings.SHELFROW][Settings.SHELFCOLUMN];
        for(int i=0; i< Settings.SHELFROW; i++){
            for(int j=0; j<Settings.SHELFCOLUMN; j++){
                matrix[i][j] = new Tile("test", Tile.Color.NULLTILE);
            }
        }
        matrix[0][0].setColor(Tile.Color.PINK);
        matrix[0][2].setColor(Tile.Color.BLUE);
        matrix[1][4].setColor(Tile.Color.GREEN);
        matrix[2][3].setColor(Tile.Color.WHITE);
        matrix[3][1].setColor(Tile.Color.YELLOW);
        matrix[5][2].setColor(Tile.Color.LIGHTBLUE);
        Shelf shelf = new Shelf(matrix);
        testPlayer.setMyShelf(shelf);
        assertEquals(testCard.checkPersonalGoal(testPlayer.getMyShelf()), 12);
        matrix[0][0].setColor(Tile.Color.PINK);
        matrix[0][2].setColor(Tile.Color.BLUE);
        matrix[1][4].setColor(Tile.Color.GREEN);
        matrix[2][3].setColor(Tile.Color.WHITE);
        matrix[3][1].setColor(Tile.Color.YELLOW);
        matrix[5][2].setColor(Tile.Color.GREEN);
        Shelf shelf1 = new Shelf(matrix);
        testPlayer.setMyShelf(shelf1);
        assertEquals(testCard.checkPersonalGoal(testPlayer.getMyShelf()), 9);
        matrix[0][0].setColor(Tile.Color.PINK);
        matrix[0][2].setColor(Tile.Color.BLUE);
        matrix[1][4].setColor(Tile.Color.GREEN);
        matrix[2][3].setColor(Tile.Color.WHITE);
        matrix[3][1].setColor(Tile.Color.GREEN);
        matrix[5][2].setColor(Tile.Color.GREEN);
        Shelf shelf2 = new Shelf(matrix);
        testPlayer.setMyShelf(shelf2);
        assertEquals(testCard.checkPersonalGoal(testPlayer.getMyShelf()), 6);
        matrix[0][0].setColor(Tile.Color.PINK);
        matrix[0][2].setColor(Tile.Color.BLUE);
        matrix[1][4].setColor(Tile.Color.GREEN);
        matrix[2][3].setColor(Tile.Color.GREEN);
        matrix[3][1].setColor(Tile.Color.GREEN);
        matrix[5][2].setColor(Tile.Color.GREEN);
        Shelf shelf3 = new Shelf(matrix);
        testPlayer.setMyShelf(shelf3);
        assertEquals(testCard.checkPersonalGoal(testPlayer.getMyShelf()), 4);
        matrix[0][0].setColor(Tile.Color.PINK);
        matrix[0][2].setColor(Tile.Color.BLUE);
        matrix[1][4].setColor(Tile.Color.BLUE);
        matrix[2][3].setColor(Tile.Color.GREEN);
        matrix[3][1].setColor(Tile.Color.GREEN);
        matrix[5][2].setColor(Tile.Color.GREEN);
        Shelf shelf4 = new Shelf(matrix);
        testPlayer.setMyShelf(shelf4);
        assertEquals(testCard.checkPersonalGoal(testPlayer.getMyShelf()), 2);
        matrix[0][0].setColor(Tile.Color.PINK);
        matrix[0][2].setColor(Tile.Color.YELLOW);
        matrix[1][4].setColor(Tile.Color.BLUE);
        matrix[2][3].setColor(Tile.Color.GREEN);
        matrix[3][1].setColor(Tile.Color.GREEN);
        matrix[5][2].setColor(Tile.Color.GREEN);
        Shelf shelf5 = new Shelf(matrix);
        testPlayer.setMyShelf(shelf5);
        assertEquals(testCard.checkPersonalGoal(testPlayer.getMyShelf()), 1);
    }

    @Test
    @DisplayName("getIndex test")
    void getIndexTest(){
        PersonalGoalCard card1 = new PersonalGoalCard("/graphics/personalGoalCards/Personal_Goals1.png");
        assertEquals(1, card1.getIndex());

        PersonalGoalCard card2 = new PersonalGoalCard("/graphics/personalGoalCards/Personal_Goals2.png");
        assertEquals(2, card2.getIndex());

        PersonalGoalCard card3 = new PersonalGoalCard("/graphics/personalGoalCards/Personal_Goals3.png");
        assertEquals(3, card3.getIndex());

        PersonalGoalCard card4 = new PersonalGoalCard("/graphics/personalGoalCards/Personal_Goals4.png");
        assertEquals(4, card4.getIndex());

        PersonalGoalCard card5 = new PersonalGoalCard("/graphics/personalGoalCards/Personal_Goals5.png");
        assertEquals(5, card5.getIndex());

        PersonalGoalCard card6 = new PersonalGoalCard("/graphics/personalGoalCards/Personal_Goals6.png");
        assertEquals(6, card6.getIndex());

        PersonalGoalCard card7 = new PersonalGoalCard("/graphics/personalGoalCards/Personal_Goals7.png");
        assertEquals(7, card7.getIndex());

        PersonalGoalCard card8 = new PersonalGoalCard("/graphics/personalGoalCards/Personal_Goals8.png");
        assertEquals(8, card8.getIndex());

        PersonalGoalCard card9 = new PersonalGoalCard("/graphics/personalGoalCards/Personal_Goals9.png");
        assertEquals(9, card9.getIndex());

        PersonalGoalCard card10 = new PersonalGoalCard("/graphics/personalGoalCards/Personal_Goals10.png");
        assertEquals(10, card10.getIndex());

        PersonalGoalCard card11 = new PersonalGoalCard("/graphics/personalGoalCards/Personal_Goals11.png");
        assertEquals(11, card11.getIndex());

        PersonalGoalCard card12 = new PersonalGoalCard("/graphics/personalGoalCards/Personal_Goals12.png");
        assertEquals(12, card12.getIndex());
    }

}

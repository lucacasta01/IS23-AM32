package test;
import static org.junit.jupiter.api.Assertions.*;

import it.polimi.myShelfie.model.Player;
import it.polimi.myShelfie.model.cards.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CheckSharedGoalTest {
    private final String fakeIP = "192.168.1.1";
    private final String fakePath = "testPath";
    private final String fakeName = "testName";
    @Test
    @DisplayName("addPlayer method")
    void addPlayer(){
        int playerNumber = 2;
        SharedGoalCard checkSharedGoal = new SharedGoal1Card(fakePath, playerNumber);
        Player p1 = new Player(fakeName);
        Player p2 = new Player(fakeName);
        Player p3 = new Player(fakeName);
        checkSharedGoal.addPlayer(p1);
        assertTrue(checkSharedGoal.isAchieved(p1));
        checkSharedGoal.addPlayer(p2);
        assertTrue(checkSharedGoal.isAchieved(p2));
        checkSharedGoal.addPlayer(p3);
        assertTrue(checkSharedGoal.isAchieved(p3));
    }

    @Test
    @DisplayName("isAchieved method")
    void isAchieved() {
        int playerNumber = 2;
        SharedGoalCard checkSharedGoal = new SharedGoal1Card(fakePath, playerNumber);
        Player p = new Player(fakeName);
        assertFalse(checkSharedGoal.isAchieved(p));
        checkSharedGoal.addPlayer(p);
        assertTrue(checkSharedGoal.isAchieved(p));
    }

    @Test
    @DisplayName("popPointToken method")
    void popPointToken(){
        int playerNumber = 2;
        SharedGoalCard checkSharedGoal = new SharedGoal1Card(fakePath, playerNumber);
        assertEquals(8, checkSharedGoal.popPointToken());
        assertEquals(6, checkSharedGoal.popPointToken());
        assertEquals(4, checkSharedGoal.popPointToken());
    }
}
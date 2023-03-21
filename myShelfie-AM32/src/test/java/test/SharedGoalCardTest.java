package test;
import static org.junit.jupiter.api.Assertions.*;

import it.polimi.myShelfie.model.Player;
import it.polimi.myShelfie.model.cards.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SharedGoalCardTest {
    @Test
    @DisplayName("addPlayer method")
    void addPlayer(){
        SharedGoalCard sharedGoalCard = new SharedGoal1Card("testCard");
        Player p1 = new Player("Test1", "192.168.1.1");
        Player p2 = new Player("Test2", "192.168.1.2");
        Player p3 = new Player("Test3", "192.168.1.3");
        sharedGoalCard.addPlayer(p1);
        assertTrue(sharedGoalCard.isAchieved(p1));
        sharedGoalCard.addPlayer(p2);
        assertTrue(sharedGoalCard.isAchieved(p2));
        sharedGoalCard.addPlayer(p3);
        assertTrue(sharedGoalCard.isAchieved(p3));
    }

    @Test
    @DisplayName("isAchieved method")
    void isAchieved() {
        SharedGoalCard sharedGoalCard = new SharedGoal1Card("testCard");
        Player p = new Player("Test", "192.168.1.1");
        assertFalse(sharedGoalCard.isAchieved(p));
        sharedGoalCard.addPlayer(p);
        assertTrue(sharedGoalCard.isAchieved(p));
    }

    @Test
    @DisplayName("popPointToken method")
    void popPointToken(){
        SharedGoalCard sharedGoalCard = new SharedGoal1Card("testCard");
        assertEquals(8, sharedGoalCard.popPointToken());
        assertEquals(6, sharedGoalCard.popPointToken());
        assertEquals(4, sharedGoalCard.popPointToken());
    }
}
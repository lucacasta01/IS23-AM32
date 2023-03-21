package it.polimi.myShelfie.model.cards;

import it.polimi.myShelfie.model.Player;

public interface SharedGoalCard {
    boolean checkPattern(Player p);
     Integer popPointToken();
    boolean isAchieved(Player p);
    void addPlayer(Player p);
}

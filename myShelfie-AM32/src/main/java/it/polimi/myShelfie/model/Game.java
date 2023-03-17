package it.polimi.myShelfie.model;

import it.polimi.myShelfie.model.cards.PersonalGoalCard;
import it.polimi.myShelfie.model.cards.SharedGoalCard;

public class Game {
    enum GameStatus{
        WAITINGFORPLAYERS,
        INPROGRESS,
        LASTITERATION,
        EXCEPTIONSTATUS,
        GAMEENDEDNORMALLY
    }
    private int playersNumber;
    private Player[] players;
    private int currentPlayer;
    private Board gameBoard;

    PersonalGoalCard[] personalDeck;
    SharedGoalCard[] sharedDeck;
    SharedGoalCard[] actualSharedGoal;
    Tile[] tileHeap;

    private GameStatus gameStatus;

}

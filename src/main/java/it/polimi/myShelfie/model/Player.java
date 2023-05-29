package it.polimi.myShelfie.model;
import it.polimi.myShelfie.model.cards.PersonalGoalCard;
public class Player {

    // private Shelf myShelf;
    private int score;
    private final String username;
    private Shelf myShelf;

    private PersonalGoalCard myGoalCard;

    public Player(String name) {
        username = name;
        score = 0;
        myShelf = new Shelf();
        myGoalCard = null;
    }

    public void setGoalCard(PersonalGoalCard goalCard){
        myGoalCard = goalCard;
    }

    public void setMyShelf(Shelf myShelf){
        this.myShelf = myShelf;
    }

    /**
     * Returns the player's shelf
     *
     * @return player's shelf (shelf)
     */
    public Shelf getMyShelf() {
        return myShelf;
    }

    /**
     * Returns the player's score
     *
     * @return player's score (integer)
     */
    public int getScore() {
        return score;
    }

    /**
     * Sets the player's score to a given integer
     *
     * @param score (integer)
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * Returns the player's username
     *
     * @return player's username (String)
     */
    public String getUsername() {
        return username;
    }

    public PersonalGoalCard getMyGoalCard() {
        return myGoalCard;
    }

    public void initShelf(){
        this.myShelf  = new Shelf();
    }
    public synchronized void updateScore(int toAdd){
        this.score += toAdd;
    }

    @Override
    public String toString(){
        return username+"\n"+score;
    }

}

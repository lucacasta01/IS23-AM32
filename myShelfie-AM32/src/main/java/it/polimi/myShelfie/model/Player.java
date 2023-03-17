package it.polimi.myShelfie.model;

import it.polimi.myShelfie.model.cards.Card;

public class Player {

    // private Shelf myShelf;
    private int score;
    private final String ipAddress;
    private final String username;
    private int failedPings;
    private Shelf myShelf;

    private Card[] myCards;

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
     * Returns the player's ipAddress
     *
     * @return player's ip address (String)
     */
    public String getIpAddress() {
        return ipAddress;
    }

    /**
     * Returns the player's username
     *
     * @return player's username (String)
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the amount of times a ping to the player's ip failed
     *
     * @return number of failed attempts (integer)
     */
    public int getFailedPings() {
        return failedPings;
    }

    /**
     * Increases the failed pings counter by one
     */
    public void addFailedPing() {
        this.failedPings++;
    }

    public Player(String name, String addr) {
        username = name;
        score = 0;
        ipAddress = addr;
    }

    public static void main(String[] args) {
        Player test = new Player("andrea", "128.0.0.1");
        System.out.println("Score: " + test.getScore());
        System.out.println("Username: " + test.getUsername());
        System.out.println("Ip address: " + test.getIpAddress());
        System.out.println("FailedPings: " + test.getFailedPings());
        System.out.println("Adding a failed ping");
        test.addFailedPing();
        System.out.println("FailedPings: " + test.getFailedPings());
        System.out.println("Setting score to 5..");
        test.setScore(5);
        System.out.println("New Score: " + test.getScore());
    }
}

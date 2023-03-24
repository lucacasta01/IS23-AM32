package it.polimi.myShelfie.model;

public class Main {
    public static void main(String[] args) {
        Game game2 = new Game(2);
        Game game3 = new Game(3);
        Game game4 = new Game(4);

        System.out.println(game2.getGameBoard().toString()+"\n\n");
        System.out.println(game3.getGameBoard().toString()+"\n\n");
        System.out.println(game4.getGameBoard().toString());
    }
}

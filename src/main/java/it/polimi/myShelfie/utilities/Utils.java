package it.polimi.myShelfie.utilities;
import it.polimi.myShelfie.application.Server;
import it.polimi.myShelfie.utilities.pojo.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
public class Utils {
    public static String UIDGenerator(){
        StringBuilder stringBuilder = new StringBuilder();
        Random random = new Random();
        int r;
        do {
            for (int i = 0; i < 8; i++) {
                r = random.nextInt(48, 123);
                while ((r > 57 && r < 65) || (r> 90 && r< 96)) {
                    r = random.nextInt(48, 123);
                }
                if(r==96){
                    r++;
                }
                stringBuilder.append((char) r);
            }
        }while(Server.getInstance().getUserGame().containsValue(stringBuilder.toString()));

        return stringBuilder.toString();
    }

    public static boolean checkNicknameFormat(String nickname){
        return !nickname.contains(" ") && !nickname.equals("\n") && !nickname.equals("/");
    }

    public static void printTUIView(View view){
        //printing console view
        //shelves
        for (String s : view.getShelves()) {
            System.out.println(s + "\n");
        }
        //personal card
        System.out.println(ANSI.ITALIC + "Personal goal card:" + ANSI.RESET_STYLE);
        System.out.println(view.getPersonalCard());

        //shared cards
        for (int i = 0; i < view.getSharedCards().size(); i++) {
            System.out.println(ANSI.ITALIC + "Shared goal " + (i + 1) + ": " + ANSI.RESET_STYLE);
            System.out.println(view.getSharedCards().get(i) + "\n");
        }
        //board
        System.out.println(ANSI.ITALIC + "Board:" + ANSI.RESET_STYLE);
        System.out.println(view.getBoard() + "\n");
        //current player
        System.out.println(ANSI.ITALIC + "Turn of: " + ANSI.RESET_STYLE + view.getCurrentPlayer());
    }
}

package it.polimi.myShelfie.utilities;
import it.polimi.myShelfie.application.Server;

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

    public static String removeANSI(String s){
        List<String> ansiList = new ArrayList<>();
        ansiList.add(ANSI.BLUE);
        ansiList.add(ANSI.PURPLE);
        ansiList.add(ANSI.GREEN);
        ansiList.add(ANSI.YELLOW);
        ansiList.add(ANSI.ITALIC);
        ansiList.add(ANSI.BOLD);
        ansiList.add(ANSI.RESET_STYLE);
        ansiList.add(ANSI.RESET_COLOR);
        ansiList.add(ANSI.RED);

        for(String ansi : ansiList){
            if(s.contains(ansi)) {
                s = s.replaceAll(ansi, "");
            }
        }

        return s;
    }
}

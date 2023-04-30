package it.polimi.myShelfie.utilities;

import it.polimi.myShelfie.application.Server;

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
}

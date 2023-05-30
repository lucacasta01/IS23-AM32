package it.polimi.myShelfie.application;


import java.io.BufferedReader;
import java.io.InputStreamReader;

public class MyShelfieClient {
    private static String message;
    private static BufferedReader inReader ;

    /**
     * The main process for our game
     * @param args
     */
    public static void main(String[] args) {
        System.out.println("***MY SHELFIE***");
        System.out.println("Select your interface [TUI/GUI]");
        inReader =  new BufferedReader(new InputStreamReader(System.in));
        try{
            message = inReader.readLine();
        }catch (Exception e){
            e.printStackTrace();
        }
        message = message.toUpperCase();
        while (!message.equalsIgnoreCase("TUI")&&!message.equalsIgnoreCase("GUI")){
            System.out.println("Type the right key...");
            try{
                message = inReader.readLine();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        if(message.equals("TUI")){
            try {
                Client.main(args);
            }catch (Exception e){
                System.out.println("Error while launching TUI client");
            }
        }else{
            try{
                GUIClient.main(args);
            }catch(Exception e){
                System.out.println("Error while launching GUI client");
                e.printStackTrace();
            }
        }
    }
}

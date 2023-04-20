package it.polimi.myShelfie.server;
import it.polimi.myShelfie.utilities.Constants;

import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.io.*;

public class Client implements Runnable{

    private BufferedReader in;

    private PrintWriter out;

    private Socket client;

    private boolean done;

    @Override
    public void run() {
        try{
            try {
                client = new Socket(Constants.SERVER_IP, Constants.PORT);
            }
            catch (ConnectException connectException){
                System.out.println("Server not found");
                if(in != null){
                    try {
                        in.close();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                if(out!=null){
                    out.close();
                }
                throw new RuntimeException();
            }
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new PrintWriter(client.getOutputStream(), true);

            InputHandler inHandler = new InputHandler();
            Thread t = new Thread(inHandler);
            t.start();

            String inMessage;
            while((inMessage = in.readLine()) != null){
                System.out.println(inMessage);
            }
        }
        catch(Exception e){
            throw new RuntimeException();
        }
    }

    public void shutdown(){
        done = true;
        try{
            in.close();
            out.close();
            if(!client.isClosed()){
                client.close();
            }
        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        try {
            client.run();
        }
        catch(RuntimeException e){
            System.out.println("Closing");

        }
    }

    class InputHandler implements Runnable {

        public void run() {
            BufferedReader inReader = new BufferedReader(new InputStreamReader(System.in));
            while(!done){
                try {
                    String message = inReader.readLine();
                    if(message.equals("/quit")){
                        out.println(message);
                        System.out.println("quitting...");
                        inReader.close();
                        shutdown();
                    }
                    else {
                        out.println(message);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        }
    }
}

package it.polimi.myShelfie.controller;
import java.io.PrintWriter;
import java.net.Socket;
import java.io.*;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.server.*;
import java.util.Locale;

public class ClientImp implements Runnable{

    private BufferedReader in;

    private PrintWriter out;

    private Socket client;

    private boolean done;

    @Override
    public void run() {
        try{
            client = new Socket("127.0.0.1", 1234);
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
            shutdown();
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
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        ClientImp client = new ClientImp();
        client.run();
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

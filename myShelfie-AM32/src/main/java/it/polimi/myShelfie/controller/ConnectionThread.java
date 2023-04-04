package it.polimi.myShelfie.controller;
import java.io.*;
import java.net.*;
import java.rmi.*;
import java.rmi.registry.*;

public class ConnectionThread extends Thread implements Runnable {
    private Socket clientSocket;
    private String nickname;
    private Registry registry;
    private boolean isRMI = false;
    private Remote client;
    private BufferedReader in;
    private PrintWriter out;


    public ConnectionThread(Socket clientSocket, Registry registry){
        this.clientSocket = clientSocket;
        this.registry = registry;
    }

    public void shutdown() {
        try {
            in.close();
            out.close();
            if (!clientSocket.isClosed()) {
                clientSocket.close();
            }
        }
        catch(Exception e){

        }
    }

    public void run(){
        try{
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);
        }
        catch(Exception e){
            System.err.println("Exception throws during stream creation: " + e.toString());
            e.printStackTrace();
        }
        try{
            out.println("Please insert your username");
            nickname = in.readLine();
            System.out.println(nickname + " connected to the server");
            Server myServer = Server.getInstance();
            myServer.broadcastMessage(nickname + " joined");
            String message;
            while((message = in.readLine()) != null){
                if(message.startsWith("/nick")){

                }
                else if(message.startsWith("/quit")){
                    shutdown();
                }
                else {
                    Server.getInstance().broadcastMessage(nickname + ": " + message);
                }
            }
        }
        catch(Exception e){
            System.err.println("Exception throws while handling connection");
            e.printStackTrace();
        }
    }

    /*
    public void setRMI(boolean isRMI){
        this.isRMI = isRMI;
        if(isRMI){
            try{
                client = new ClientImp(this);
                registry.rebind("client", client);
            }
            catch(Exception e){
                System.err.println("Error during client rebinding: " + e.toString());
                e.printStackTrace();
            }
        }
        else {
            try{
                registry.unbind("client");
            }
            catch(Exception e){
                System.err.println("Error during client unbinding: "+ e.toString());
            }
        }
    }

    private void handleTCPCom(Object input){
        if(input instanceof String){
            String message = (String) input;
            if(message.equalsIgnoreCase("switch to rmi")) {
                sendMessage("Switching to RMI communication");
                setRMI(true);
            }
            else {sendMessage("Received message via TCP "+ message);}
        }
        else {
            sendMessage("Invalid message received via TCP");
        }
    }

    private void handleRMICom(Object input){
        if(input instanceof String){
            String message = (String) input;
            if(message.equalsIgnoreCase("switch to tcp")){
                sendMessage("Switching to TCP communication");
                setRMI(false);
            }
            else {
                sendMessage("Received message via RMI" +  message);
            }
        }
        else {
            sendMessage("Invalid message received via RMI");
        }
    }
    */

    public synchronized void sendMessage(String message){
        try{
            out.println(message);
        }
        catch(Exception e){
            System.out.println("Error occurred while sending a message: " + e.toString());
            e.printStackTrace();
        }
    }
}

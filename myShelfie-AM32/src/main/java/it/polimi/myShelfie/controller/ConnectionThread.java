package it.polimi.myShelfie.controller;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.util.Locale;
public class ConnectionThread extends Thread implements Runnable {
    private Socket clientSocket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private Registry registry;
    private boolean isRMI = false;
    private Remote client;

    public ConnectionThread(Socket clientSocket, Registry registry){
        this.clientSocket = clientSocket;
        this.registry = registry;
    }

    public void run(){
        try{
            inputStream = new ObjectInputStream(clientSocket.getInputStream());
            outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
        }
        catch(Exception e){
            System.err.println("Exception throws during stream creation: " + e.toString());
            e.printStackTrace();
        }
        try{
            sendMessage("WELCOME TO THE SERVER");
            Object input;
            while((input = inputStream.readObject()) != null){
                if(isRMI){
                    handleRMICom(input);
                }
                else {
                    handleTCPCom(input);
                }
            }
            inputStream.close();
            outputStream.close();
            clientSocket.close();
        }
        catch(Exception e){
            System.err.println("Exception throws while handling connection");
            e.printStackTrace();
        }
    }

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

    public synchronized void sendMessage(String message){
        try{
            outputStream.writeObject(message);
            outputStream.flush();
        }
        catch(Exception e){
            System.out.println("Error occurred while sending a message: " + e.toString());
            e.printStackTrace();
        }
    }
}

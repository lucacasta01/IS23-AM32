package it.polimi.myShelfie.server;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.polimi.myShelfie.model.Position;
import it.polimi.myShelfie.utilities.Constants;
import it.polimi.myShelfie.utilities.JsonParser;
import it.polimi.myShelfie.utilities.beans.Action;
import it.polimi.myShelfie.utilities.beans.Response;

import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Client implements Runnable{

    private BufferedReader in;

    private PrintWriter out;

    private Socket client;
    private String nickname;

    private boolean done;
    private boolean configurationDone = false;
    private Response response;
    private boolean validRecieved = false;

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
            Response response = recieveResponse(inMessage);
                if(response.getResponseType() == Response.ResponseType.INFO){
                    System.out.println(response.getInfoMessage());
                }
                else if(response.getResponseType() == Response.ResponseType.CHATMESSAGE){
                    System.out.println("*NEW CHAT MESSAGE* : "+response.getChatMessage().getSender()+": "+response.getChatMessage().getMessage());
                }
                else if(response.getResponseType() == Response.ResponseType.VALID){
                    validRecieved = true;
                    System.out.println(response.getInfoMessage());
                }
                else if(response.getResponseType() == Response.ResponseType.DENIED){
                    System.out.println(response.getInfoMessage());
                }
                else if (response.getResponseType() == Response.ResponseType.UPDATE) {
                    //UPDATE THE VIEW
                }else if(response.getResponseType() == Response.ResponseType.PING){
                  //ping messages are thrown away
                }
                else if(response.getResponseType() == Response.ResponseType.SHUTDOWN){
                    System.out.println(response.getInfoMessage());
                    shutdown();
                }
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
            System.exit(0);
        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }

    public Thread pingThread(){
        return new Thread(){
          public void run(){
              while(true) {
                  try {
                      sendAction(new Action(Action.ActionType.PING, null, null, null, null, null));
                  } catch (IOException e) {
                      if(client.isConnected()) {
                          System.exit(10);
                      }
                  }
                  try {
                      sleep(1000);
                  } catch (InterruptedException e) {
                      throw new RuntimeException(e);
                  }
              }
          }
        };
    }

    public static void main(String[] args) {
        Client client = new Client();
        //client.pingThread().start();
        try {
            client.run();
        }
        catch(RuntimeException e){
            System.out.println("Closing");
            System.exit(0);
        }

    }

    class InputHandler implements Runnable {

        public void run() {

            String message;
            BufferedReader inReader = new BufferedReader(new InputStreamReader(System.in));
            while(!done){
                try {
                    message = inReader.readLine();

                    if(message.equals("/quit")){
                        Action a = new Action(Action.ActionType.QUIT, nickname, "", "", null, null);
                        sendAction(a);
                        out.println(message);
                        System.out.println("quitting...");
                        inReader.close();
                        shutdown();
                    }
                    else if(message.startsWith("/chat")){
                        Action a = new Action(Action.ActionType.CHAT,nickname,message.substring(message.indexOf("/chat") + "/chat ".length()),"",null,null);
                        sendAction(a);
                    }
                    else if(message.startsWith("/collect")){
                        int firstTile = message.indexOf("/collect") + "/collect ".length();
                        String substr = message.substring(firstTile);
                        String[] pos = substr.split(",");
                        List<Position> tilesSelected = new ArrayList<>();
                        for(String s : pos){
                            tilesSelected.add(new Position(Integer.parseInt(s.substring(0,0)), Integer.parseInt(s.substring(1))));
                        }
                        Action a = new Action(Action.ActionType.PICKTILES,nickname,"","",tilesSelected,null);
                        sendAction(a);
                    }
                    else if(message.startsWith("/column")){
                        int index = message.indexOf("/collect") + "/collect ".length();
                        String substr = message.substring(index);
                        int col = Integer.parseInt(substr.substring(0,0));
                        Action a = new Action(Action.ActionType.SELECTCOLUMN,nickname,"","",null,col);
                        sendAction(a);
                    }
                    else {
                        Action a = new Action(Action.ActionType.INFO,nickname,"",message,null,null);
                        sendAction(a);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        }
    }
    private Response recieveResponse(String jString) throws IOException {
        return JsonParser.getResponse(jString);
    }

    private synchronized void sendAction(Action action) throws IOException {
        Gson gson = new Gson();
        if(out!=null) {
            out.println(gson.toJson(action));
        }
    }

}

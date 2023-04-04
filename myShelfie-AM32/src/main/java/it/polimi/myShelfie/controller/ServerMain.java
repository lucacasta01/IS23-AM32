package it.polimi.myShelfie.controller;

public class ServerMain {
    public static void main(String[] args){
        Server server = Server.getInstance();
        server.run();
    }
}

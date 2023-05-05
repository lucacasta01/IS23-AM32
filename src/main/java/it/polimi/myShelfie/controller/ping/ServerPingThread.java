package it.polimi.myShelfie.controller.ping;

import it.polimi.myShelfie.application.Server;
import it.polimi.myShelfie.controller.ClientHandler;
import it.polimi.myShelfie.utilities.Constants;
import it.polimi.myShelfie.utilities.PingObject;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class ServerPingThread extends Thread{
    protected final Server server = Server.getInstance();
    protected final ClientHandler ch;


    public ServerPingThread(ClientHandler ch){
        this.ch = ch;
    }

    public synchronized void setElapsed(){}

    public synchronized boolean getElapsed(){
        return false;
    }

}

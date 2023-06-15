package it.polimi.myShelfie.network.ping;
import it.polimi.myShelfie.application.Server;
import it.polimi.myShelfie.network.ClientHandler;
public abstract class ServerPingThread extends Thread{
    protected final Server server = Server.getInstance();
    protected final ClientHandler ch;
    public ServerPingThread(ClientHandler ch){
        this.ch = ch;
    }
    public synchronized void setElapsed(){}
    public synchronized void setKill(boolean kill){}
    public synchronized boolean getElapsed(){
        return false;
    }
}
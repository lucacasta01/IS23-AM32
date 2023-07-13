package it.polimi.myShelfie.network.ping;
import it.polimi.myShelfie.application.Server;
import it.polimi.myShelfie.network.Lobby;
import it.polimi.myShelfie.network.ClientHandler;
import it.polimi.myShelfie.utilities.Settings;

import java.rmi.RemoteException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServerRmiPingThread extends ServerPingThread{
    public final AtomicBoolean kill = new AtomicBoolean(false);
    public ServerRmiPingThread(ClientHandler ch) {
        super(ch);
    }

    @Override
    public synchronized void setKill(boolean kill) {
        this.kill.set(kill);
    }

    @Override
    public void run() {
        kill.set(false);
        boolean pingFailed = false;
        while(!pingFailed&&!kill.get()) {
            try {
                Timer timer = new Timer(true);
                TimerTask interruptTimerTask = new InterruptTimerTask(Thread.currentThread(), ch, server);
                timer.schedule(interruptTimerTask, 15000);
                ch.getRmiClient().ping();
                timer.cancel();
            } catch (RemoteException e) { //ping failed
                pingFailed = true;
            }
            try {
                Thread.sleep(Settings.PINGPERIOD);
            } catch (InterruptedException e) {
                pingFailed = true;
            }
        }
    }


    private static class InterruptTimerTask extends TimerTask {
        private Thread thread;
        private ClientHandler ch;
        private Server server;

        public InterruptTimerTask(Thread thread, ClientHandler ch, Server server) {
            this.thread=thread;
            this.ch = ch;
            this.server = server;
        }

        @Override
        public void run(){
            System.err.println("Ping failed for " + ch.getNickname());
            if (ch.isPlaying()) {
                Lobby l =server.lobbyOf(ch);
                if(l!=null){
                    l.getLobbyPlayers().remove(ch);
                    server.killLobby(l.getLobbyUID());
                }
            }
            ch.shutdown();
            server.removeClient(ch);
            if (server.getUserGame() != null) {
                if (server.getUserGame().get(ch.getNickname()) != null) {
                    if (server.getUserGame().get(ch.getNickname()).equals("-")) {
                        server.getUserGame().remove(ch.getNickname());
                    }
                }
            }
            synchronized (server.getUserGame()) {
                server.saveUserGame();
            }
            thread.interrupt();
        }
    }
}



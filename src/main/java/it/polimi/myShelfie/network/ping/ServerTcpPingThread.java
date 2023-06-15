package it.polimi.myShelfie.network.ping;
import it.polimi.myShelfie.network.ClientHandler;
import it.polimi.myShelfie.utilities.Settings;
import it.polimi.myShelfie.utilities.PingObject;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;


//todo seems broken

public class ServerTcpPingThread extends ServerPingThread{

    private final AtomicBoolean elapsed;

    public ServerTcpPingThread(ClientHandler ch) {
        super(ch);
        this.elapsed = new AtomicBoolean(false);
    }

    @Override
    public synchronized void setElapsed() {
        this.elapsed.set(true);
    }
    @Override
    public synchronized boolean getElapsed(){
        return elapsed.get();
    }

    @Override
    public void run() {
        synchronized (ch.locker){
            try {
                ch.locker.wait();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        while (!getElapsed()) {
            try {
                ch.sendPing();
            } catch (IOException e) {
                System.exit(10);
            }
            SwapElapsed swapElapsed = new SwapElapsed(ch);
            swapElapsed.start();
            while (ch.getPongResponses().size() == 0) {
                synchronized (ch.getPongResponses()) {
                    try {
                        ch.getPongResponses().wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }


            if (ch.getPongResponses().get(0).isElapsed()) {
                System.err.println("Ping failed for " + ch.getNickname() + "");

                if (ch.isPlaying()) {
                    if(server.lobbyOf(ch)!=null) {
                        server.killLobby(server.lobbyOf(ch).getLobbyUID());
                    }
                    ch.shutdown();
                    server.removeClient(ch);
                } else {
                    ch.shutdown();
                    server.removeClient(ch);
                }

                setElapsed();
            } else {
                try {
                    swapElapsed.setRunning(false);
                } catch (Exception e) {
                    //ignore
                }
                synchronized (ch.getPongResponses()) {
                    ch.getPongResponses().remove(0);
                }
                try {
                    Thread.sleep(Settings.PINGPERIOD);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
        }
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
    }

    static class SwapElapsed extends Thread {

        private boolean isRunning = true;
        private final ClientHandler client;
        public SwapElapsed(ClientHandler ch){
            this.client = ch;
        }

        public boolean isRunning() {
            return isRunning;
        }

        public void setRunning(boolean running) {
            isRunning = running;
        }

        @Override
        public void run() {
            int time = 0;
            while (isRunning() && time < Settings.PINGTHRESHOLD) {
                try {
                    Thread.sleep(Settings.PINGTHRESHOLD/ Settings.PINGFACTOR);
                    time += Settings.PINGTHRESHOLD/ Settings.PINGFACTOR;
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            if (isRunning()) {
                synchronized (client.getPongResponses()) {
                    client.getPongResponses().add(new PingObject(true));
                    client.getPongResponses().notifyAll();
                }
            }
        }
    }
}

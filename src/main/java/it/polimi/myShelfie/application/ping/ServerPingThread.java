package it.polimi.myShelfie.application.ping;

import it.polimi.myShelfie.application.Server;
import it.polimi.myShelfie.controller.ClientHandler;
import it.polimi.myShelfie.utilities.Constants;
import it.polimi.myShelfie.utilities.PingObject;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServerPingThread extends Thread{
    private final Server server = Server.getInstance();
    private final ClientHandler ch;
    private final AtomicBoolean elapsed;

    public ServerPingThread(ClientHandler ch){
        this.ch = ch;
        this.elapsed = new AtomicBoolean(false);
    }

    public synchronized void setElapsed() {
        this.elapsed.set(true);
    }

    public synchronized boolean getElapsed(){
        return elapsed.get();
    }

    @Override
    public void run() {
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
                    server.killLobby(server.lobbyOf(ch).getLobbyUID());
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
                    Thread.sleep(Constants.PINGPERIOD);
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

    class SwapElapsed extends Thread {

        private boolean isRunning = true;
        private ClientHandler client;
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
            while (isRunning() && time < Constants.PINGTHRESHOLD) {
                try {
                    Thread.sleep(Constants.PINGTHRESHOLD/Constants.PINGFACTOR);
                    time += Constants.PINGTHRESHOLD/Constants.PINGFACTOR;
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

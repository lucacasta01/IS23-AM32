package it.polimi.myShelfie.controller.ping;
import it.polimi.myShelfie.controller.ClientHandler;
import it.polimi.myShelfie.utilities.Constants;
import java.rmi.RemoteException;
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
                ch.getRmiClient().ping();
            } catch (RemoteException e) { //ping failed
                pingFailed = true;
                System.err.println("Ping failed for " + ch.getNickname() + "");
                if (ch.isPlaying()) {
                    server.killLobby(server.lobbyOf(ch).getLobbyUID());
                    ch.shutdown();
                    server.removeClient(ch);
                } else {
                    ch.shutdown();
                    server.removeClient(ch);
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
            try {
                Thread.sleep(Constants.PINGPERIOD);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

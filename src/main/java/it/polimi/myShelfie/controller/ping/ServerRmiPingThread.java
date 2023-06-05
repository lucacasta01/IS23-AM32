package it.polimi.myShelfie.controller.ping;
import it.polimi.myShelfie.controller.ClientHandler;
import it.polimi.myShelfie.controller.Lobby;
import it.polimi.myShelfie.utilities.Settings;
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
            }
            try {
                Thread.sleep(Settings.PINGPERIOD);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

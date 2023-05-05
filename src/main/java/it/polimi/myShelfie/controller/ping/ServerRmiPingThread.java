package it.polimi.myShelfie.controller.ping;

import it.polimi.myShelfie.controller.ClientHandler;

import java.rmi.RemoteException;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServerRmiPingThread extends ServerPingThread{
    private AtomicBoolean pingFailed;
    public ServerRmiPingThread(ClientHandler ch) {
        super(ch);
        pingFailed = new AtomicBoolean(false);
    }

    @Override
    public void run() {
        int count = 0;
        while(count<3) {
            try {
                ch.getRmiClient().ping();
                count = 0;
            } catch (RemoteException e) {
                count++;
            }
        }
    }
}

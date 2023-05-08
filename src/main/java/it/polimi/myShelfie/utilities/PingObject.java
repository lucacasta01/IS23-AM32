package it.polimi.myShelfie.utilities;
import java.util.concurrent.atomic.AtomicBoolean;
public class PingObject {
    private final AtomicBoolean isElapsed = new AtomicBoolean(false);
    public PingObject(boolean isElapsed){
        this.isElapsed.set(isElapsed);
    }

    public boolean isElapsed() {
        return isElapsed.get();
    }

    public void setElapsed(boolean elapsed) {
        isElapsed.set(elapsed);
    }
}

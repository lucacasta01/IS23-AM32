package it.polimi.myShelfie.utilities.pojo;
import java.util.ArrayList;
import java.util.List;

/**
 * Map of nickname and game UID. Useful to find saved game on the disk.
 */
public class Usergame {
    private List<String> nicknames;
    private List<String> UIDs;

    public Usergame(){
        nicknames = new ArrayList<>();
        UIDs = new ArrayList<>();
    }

    public List<String> getNicknames() {
        return nicknames;
    }

    public List<String> getUIDs() {
        return UIDs;
    }

    public void setNicknames(List<String> nicknames) {
        this.nicknames = nicknames;
    }

    public void setUIDs(List<String> UIDs) {
        this.UIDs = UIDs;
    }
}

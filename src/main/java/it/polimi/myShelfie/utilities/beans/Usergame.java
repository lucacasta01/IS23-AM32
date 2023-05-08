package it.polimi.myShelfie.utilities.beans;
import java.util.ArrayList;
import java.util.List;
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

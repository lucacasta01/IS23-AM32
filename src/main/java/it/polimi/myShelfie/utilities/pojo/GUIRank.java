package it.polimi.myShelfie.utilities.pojo;

import java.util.ArrayList;
import java.util.List;

public class GUIRank {
    private final List<String> pos;
    private final List<String> nicknames;
    private final List<String> scores;

    public GUIRank(){
        pos = new ArrayList<>();
        nicknames = new ArrayList<>();
        scores = new ArrayList<>();
    }

    public void addPos(String p){pos.add(p);}
    public void addNickname(String n){nicknames.add(n);}
    public void addScore(String s){scores.add(s);}

    public List<String> getPos() {
        return pos;
    }

    public List<String> getNicknames() {
        return nicknames;
    }

    public List<String> getScores() {
        return scores;
    }
}

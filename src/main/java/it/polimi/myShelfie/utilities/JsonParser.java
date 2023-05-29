package it.polimi.myShelfie.utilities;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.polimi.myShelfie.utilities.beans.*;
import org.apache.commons.io.IOUtils;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class JsonParser {
    /**
     * converts the json constraint file into a list of positions
     * @param jPath path of the json file
     * @return the list of out of borders positions
     * @throws IOException
     */
    public static List<Position> getNullConfig(String jPath) throws IOException {
        try (InputStream inputStream = JsonParser.class.getResourceAsStream(jPath)) {
            String jsonString = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            Type type = new TypeToken<ArrayList<Position>>() {}.getType();
            return new Gson().fromJson(jsonString, type);
        }
    }

    public static List<ColorPosition> getPersonalGoalConfig(String jPath){
        try (InputStream inputStream = JsonParser.class.getResourceAsStream(jPath)){
            String jsonString = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            Type type = new TypeToken<ArrayList<ColorPosition>>() {}.getType();
            return new Gson().fromJson(jsonString, type);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static GameParameters getGameParameters(String jPath) throws IOException{
        try (InputStream inputStream = new FileInputStream(Objects.requireNonNull(JsonParser.class.getResource(jPath)).getPath())) {
            String jsonString = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            Type type = new TypeToken<GameParameters>() {}.getType();
            GameParameters toReturn = new Gson().fromJson(jsonString, type);
            return toReturn;
        }
    }

    public static Action getAction(String jString){
        Type type = new TypeToken<Action>(){}.getType();
        return new Gson().fromJson(jString,type);
    }

    public static Response getResponse(String jString){
        Type type = new TypeToken<Response>(){}.getType();
        return new Gson().fromJson(jString,type);
    }

    public static Map<String,String> getUsergame(String jPath){
        try (InputStream inputStream = new FileInputStream(jPath)) {
            String jsonString = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            Type type = new TypeToken<Usergame>() {}.getType();
            Usergame usergame = new Gson().fromJson(jsonString, type);
            Map<String,String> toReturn = new HashMap<>();
            int i=0;
            for(String s : usergame.getNicknames()){
                toReturn.put(s,usergame.getUIDs().get(i));
                i++;
            }
            return toReturn;
        } catch (IOException e) {
            return new HashMap<>();
        }
    }

    public static GUIRank getGUIRank(String jString){
        Type type = new TypeToken<GUIRank>(){}.getType();
        return new Gson().fromJson(jString,type);
    }

    public static String guiRankToJson(GUIRank guiRank){
        Type type = new TypeToken<GUIRank>(){}.getType();
        return new Gson().toJson(guiRank,type);
    }
}


package it.polimi.myShelfie.utilities;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.polimi.myShelfie.utilities.beans.Action;
import it.polimi.myShelfie.utilities.beans.GameParameters;
import it.polimi.myShelfie.utilities.beans.Response;
import it.polimi.myShelfie.utilities.beans.Usergame;
import org.apache.commons.io.IOUtils;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
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
        try (InputStream inputStream = new FileInputStream(Objects.requireNonNull(JsonParser.class.getResource(jPath)).toString())) {
            String jsonString = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            Type type = new TypeToken<ArrayList<Position>>() {}.getType();
            return new Gson().fromJson(jsonString, type);
        }
    }

    public static List<ColorPosition> getPersonalGoalConfig(String jPath) throws IOException{
        try (InputStream inputStream = new FileInputStream(Objects.requireNonNull(JsonParser.class.getResource(jPath)).toString())) {
            String jsonString = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            Type type = new TypeToken<ArrayList<ColorPosition>>() {}.getType();
            return new Gson().fromJson(jsonString, type);
        }
    }

    public static GameParameters getGameParameters(String jPath) throws IOException{
        try (InputStream inputStream = new FileInputStream(Objects.requireNonNull(JsonParser.class.getResource(jPath)).toString())) {
            String jsonString = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            Type type = new TypeToken<GameParameters>() {}.getType();
            GameParameters toReturn = new Gson().fromJson(jsonString, type);
            return toReturn;
        }
    }

    public static Action getAction(String jString) throws IOException{
        Type type = new TypeToken<Action>(){}.getType();
        return new Gson().fromJson(jString,type);
    }

    public static Response getResponse(String jString) throws IOException{
        Type type = new TypeToken<Response>(){}.getType();
        return new Gson().fromJson(jString,type);
    }

    public static Map<String,String> getUsergame(String jPath){
        try (InputStream inputStream = new FileInputStream(Objects.requireNonNull(JsonParser.class.getResource(jPath)).toString())) {
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
}


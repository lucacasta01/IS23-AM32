package it.polimi.myShelfie.utilities;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.polimi.myShelfie.model.Position;
import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class JsonParser {
    /**
     * converts the json constraint file into a list of positions
     * @param jPath path of the json file
     * @return the list of out of borders positions
     * @throws IOException
     */
    public static List<Position> getNullConfig(String jPath) throws IOException {
        try (InputStream inputStream = new FileInputStream(jPath)) {
            String jsonString = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            Type type = new TypeToken<ArrayList<Position>>() {}.getType();
            return new Gson().fromJson(jsonString, type);
        }
    }

    public static List<ColorPosition> getPersonalGoalConfig(String jPath) throws IOException{
        try (InputStream inputStream = new FileInputStream(jPath)) {
            String jsonString = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            Type type = new TypeToken<ArrayList<ColorPosition>>() {}.getType();
            List<ColorPosition> toReturn = new Gson().fromJson(jsonString, type);
            return toReturn;
        }
    }

    public static GameParameters getGameParameters(String jPath) throws IOException{
        try (InputStream inputStream = new FileInputStream(jPath)) {
            String jsonString = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            Type type = new TypeToken<GameParameters>() {}.getType();
            GameParameters toReturn = new Gson().fromJson(jsonString, type);
            return toReturn;
        }
    }
}


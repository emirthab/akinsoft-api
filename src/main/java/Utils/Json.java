package Utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONObject;

public class Json {
    public static String getPretty(JSONObject json){
        JsonElement jsonElement = JsonParser.parseString(json.toString());
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(jsonElement);
    }

    public static String getPretty(String json){
        JsonElement jsonElement = JsonParser.parseString(json);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(jsonElement);
    }

    public static String getPretty(JSONArray json){
        JsonElement jsonElement = JsonParser.parseString(json.toString());
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(jsonElement);
    }
}

package utils.serialization;

import com.google.gson.Gson;
import objects.configuration.Config;

public class JsonParser {
    private static final Gson GSON = new Gson();

    public static String serialize(Config item) {
        return GSON.toJson(item);
    }

    public static Config deserialize(String data) {
        return GSON.fromJson(data, Config.class);
    }
}

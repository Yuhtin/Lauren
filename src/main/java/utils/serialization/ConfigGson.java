package utils.serialization;

import com.google.gson.Gson;
import configuration.Config;

public class ConfigGson {
    private static final Gson GSON = new Gson();

    public static String serialize(Config item) {
        return GSON.toJson(item);
    }

    public static Config deserialize(String data) {
        return GSON.fromJson(data, Config.class);
    }
}

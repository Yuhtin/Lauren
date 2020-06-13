package utils.serialization;

import com.google.gson.Gson;
import models.data.PlayerData;

public class PlayerDataGson {
    private static final Gson GSON = new Gson();

    public static String serialize(PlayerData item) {
        return GSON.toJson(item);
    }

    public static PlayerData deserialize(String data) { return GSON.fromJson(data, PlayerData.class); }
}

package utils.serialization;

import com.google.gson.Gson;
import dao.controller.PlayerDataController;

public class DataGson {
    private static final Gson GSON = new Gson();

    public static String serialize(PlayerDataController item) {
        return GSON.toJson(item);
    }

    public static PlayerDataController deserialize(String data) {
        return GSON.fromJson(data, PlayerDataController.class);
    }
}

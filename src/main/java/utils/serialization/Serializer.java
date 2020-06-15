package utils.serialization;

import com.google.gson.Gson;
import draw.Draw;
import entities.Config;
import lombok.AllArgsConstructor;
import models.data.Match;
import models.data.PlayerData;

@AllArgsConstructor
public class Serializer<T> {

    public static final Serializer<Config> config = new Serializer<>(Config.class);
    public static final Serializer<PlayerData> playerData = new Serializer<>(PlayerData.class);
    public static final Serializer<Match> match = new Serializer<>(Match.class);
    public static final Serializer<Draw> draw = new Serializer<>(Draw.class);

    public Class<T> type;
    public static Gson GSON = new Gson();

    public String serialize(T type) {
        return GSON.toJson(type);
    }

    public T deserialize(String json) {
        return GSON.fromJson(json, type);
    }
}

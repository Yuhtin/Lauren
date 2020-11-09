package com.yuhtin.lauren.utils.serialization;

import com.google.gson.Gson;
import com.yuhtin.lauren.core.entities.Config;
import com.yuhtin.lauren.core.player.Player;
import com.yuhtin.lauren.core.statistics.StatsInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class Serializer<T> {

    @Getter private static final Serializer<Config> config = new Serializer<>(Config.class);
    @Getter private static final Serializer<StatsInfo> stats = new Serializer<>(StatsInfo.class);

    private final Class<T> type;
    public static final Gson GSON = new Gson();

    public String serialize(T type) {
        return GSON.toJson(type);
    }

    public T deserialize(String json) {
        return GSON.fromJson(json, type);
    }
}

package com.yuhtin.lauren.utils.serialization;

import com.google.gson.Gson;
import com.yuhtin.lauren.core.draw.Draw;
import com.yuhtin.lauren.core.entities.Config;
import com.yuhtin.lauren.core.match.Match;
import com.yuhtin.lauren.core.player.Player;
import com.yuhtin.lauren.core.statistics.StatsInfo;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Serializer<T> {

    public static final Serializer<Config> config = new Serializer<>(Config.class);
    public static final Serializer<Player> player = new Serializer<>(Player.class);
    public static final Serializer<Match> match = new Serializer<>(Match.class);
    public static final Serializer<StatsInfo> stats = new Serializer<>(StatsInfo.class);

    public Class<T> type;
    public static Gson GSON = new Gson();

    public String serialize(T type) {
        return GSON.toJson(type);
    }

    public T deserialize(String json) {
        return GSON.fromJson(json, type);
    }
}

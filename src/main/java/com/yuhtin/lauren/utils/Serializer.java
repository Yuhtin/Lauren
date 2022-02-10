package com.yuhtin.lauren.utils;

import com.google.gson.Gson;
import com.yuhtin.lauren.core.statistics.StatsInfo;
import com.yuhtin.lauren.core.vote.VoteResponse;
import com.yuhtin.lauren.core.xp.Level;
import com.yuhtin.lauren.models.objects.Config;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class Serializer<T> {

    public static final Gson GSON = new Gson();

    @Getter private static final Serializer<Config> config = new Serializer<>(Config.class);
    @Getter private static final Serializer<StatsInfo> stats = new Serializer<>(StatsInfo.class);
    @Getter private static final Serializer<VoteResponse> vote = new Serializer<>(VoteResponse.class);
    @Getter private static final Serializer<Level> level = new Serializer<>(Level.class);

    private final Class<T> type;

    public String serialize(T type) {
        return GSON.toJson(type);
    }

    public T deserialize(String json) {
        return GSON.fromJson(json, type);
    }
}

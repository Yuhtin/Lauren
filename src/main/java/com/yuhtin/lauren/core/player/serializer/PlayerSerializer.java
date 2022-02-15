package com.yuhtin.lauren.core.player.serializer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yuhtin.lauren.core.player.Player;
import com.yuhtin.lauren.core.player.impl.Entity;
import lombok.val;

public class PlayerSerializer {

    private static final Gson GSON;

    private PlayerSerializer() throws InstantiationException { throw new InstantiationException("Utility class"); }

    static {
        val gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Entity.class, new AbstractAdapter<Entity>());
        gsonBuilder.setPrettyPrinting();

        GSON = gsonBuilder.create();
    }

    public static String serialize(Player player) {
        return GSON.toJson(player);
    }

    public static Player deserialize(String data) {
        return GSON.fromJson(data, Player.class);
    }

}
package com.yuhtin.lauren.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Random;

@AllArgsConstructor
public enum Reward {

    EXPERIENCE("3000 XP", "<:xp:772285036174639124>", 25),
    RANKED_POINTS("Pontos de Patente", "\uD83D\uDCB8", 25),
    MONEY("Shard", "<:boost_emoji:772285522852839445>", 20),
    BOOST("Boost de XP", "<:beacon:771543538252120094>", 20),
    ROLE("Cargo", "<:users:772286870624272415>", 10);

    @Getter private final String name;
    @Getter private final String emoji;
    @Getter private final int chance;

    public static Reward getRandomReward() {
        for (Reward value : values()) {

            int i = new Random().nextInt(100);
            if (i < value.getChance()) return value;

        }

        return Reward.values()[new Random().nextInt(Reward.values().length)];
    }

}

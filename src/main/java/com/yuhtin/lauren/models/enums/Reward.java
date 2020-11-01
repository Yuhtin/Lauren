package com.yuhtin.lauren.models.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum Reward {

    EXPERIENCE("3000 XP", "<:xp:772285036174639124>"),
    RANKED_POINTS("Pontos de Patente", "\uD83D\uDCB8"),
    MONEY("Shard", "<:boost_emoji:772285522852839445>"),
    ROLE("Cargo", "<:users:772286870624272415>");

    @Getter private final String name;
    @Getter private final String emoji;

}

package com.yuhtin.lauren.models.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum Reward {

    EXPERIENCE("3000 XP"),
    RANKED_POINTS("Pontos de Patente"),
    MONEY("Shard"),
    ROLE("Cargo");

    @Getter private final String name;

}

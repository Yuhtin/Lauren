package com.yuhtin.lauren.models.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum GameMode {

    CASUAL("Casual"),
    RANKED("Ranked");

    public final String name;

    @Override
    public String toString() {
        return name;
    }
}

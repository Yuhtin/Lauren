package com.yuhtin.lauren.models.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum GameType {

    POOL("8BallPool", 2),
    LUDO("LudoKing", 4);

    public final String name;
    public final int minPlayers;

    @Override
    public String toString() {
        return name;
    }
}

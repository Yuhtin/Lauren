package com.yuhtin.lauren.core.match;

import com.yuhtin.lauren.models.enums.GameMode;
import com.yuhtin.lauren.models.enums.GameType;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Game {
    public final GameType type;
    public final GameMode mode;

    @Override
    public String toString() {
        return type + " " + mode;
    }
}

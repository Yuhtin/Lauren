package com.yuhtin.lauren.core.player;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SimplePlayer {

    private final long userID;
    private final int level, xp;
}

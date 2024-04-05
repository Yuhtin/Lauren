package com.yuhtin.lauren.module.impl.player;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Player {

    private final long id;
    private int points;

}

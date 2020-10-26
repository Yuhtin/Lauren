package com.yuhtin.lauren.core.xp;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Builder
@Data
public class Level {

    private final int miniumExperience;
    private final List<Long> rolesToGive = new ArrayList<>();
}

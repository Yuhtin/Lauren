package com.yuhtin.lauren.module.impl.level;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@RequiredArgsConstructor
public class Level implements Serializable {

    private final int level;
    private final int miniumExperience;

    private final List<Long> rolesToGive = new ArrayList<>();

}

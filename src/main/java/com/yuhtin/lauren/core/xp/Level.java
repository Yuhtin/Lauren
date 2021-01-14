package com.yuhtin.lauren.core.xp;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Builder
@Data
public class Level implements Serializable {

    private final transient int level;
    private final transient int miniumExperience;
    private final List<Long> rolesToGive = new ArrayList<>();

}

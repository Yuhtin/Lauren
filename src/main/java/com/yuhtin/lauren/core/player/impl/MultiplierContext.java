package com.yuhtin.lauren.core.player.impl;

import java.util.HashMap;
import java.util.Map;

public interface MultiplierContext {

    Map<String, Double> multiplierList = new HashMap<>() {
        {
            put("earnings.boost", .15);
            put("role.prime", .15);
            put("role.booster", .25);
        }
    };

    double multiply();

}

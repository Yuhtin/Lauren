package com.yuhtin.lauren.models.objects;

import java.util.HashMap;
import java.util.Map;

public interface MultiplierContext {

    Map<String, Double> multiplerList = new HashMap<String, Double>() {
        {
            put("earnings.boost", .15);
            put("role.prime", .15);
            put("role.booster", .25);
        }
    };

    double multiply();


}

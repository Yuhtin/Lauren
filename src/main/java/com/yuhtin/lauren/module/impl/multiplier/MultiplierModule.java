package com.yuhtin.lauren.module.impl.multiplier;

import com.yuhtin.lauren.Lauren;
import com.yuhtin.lauren.config.YamlConfiguration;
import com.yuhtin.lauren.module.ConfigurableModule;

import java.util.HashMap;

public class MultiplierModule extends ConfigurableModule {

    private final HashMap<String, Double> multipliers = new HashMap<>();

    @Override
    public boolean setup(Lauren lauren) {
        setConfig(YamlConfiguration.load("multipliers.yml"));
        reload();

        return true;
    }

    @Override
    public void reload() {
        super.reload();

        multipliers.clear();

        
    }
}

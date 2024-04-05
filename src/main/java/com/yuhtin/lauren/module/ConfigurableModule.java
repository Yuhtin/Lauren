package com.yuhtin.lauren.module;

import com.yuhtin.lauren.config.YamlConfiguration;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public abstract class ConfigurableModule implements Module {

    private YamlConfiguration config;

    public void reload() {
        if (config != null) {
            config.reload();
        }
    }

}

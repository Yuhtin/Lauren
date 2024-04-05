package com.yuhtin.lauren.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConfigurationSection extends FileConfiguration {

    private final String path;

    ConfigurationSection(FileConfiguration parent, String path) {
        super(parent.file, path.isBlank() ? parent.data : (Map<String, Object>) parent.data.get(path));
        this.path = path;
    }

    public List<String> getKeys(boolean includeMainPath) {
        if (includeMainPath) {
            List<String> paths = new ArrayList<>();
            for (String key : this.data.keySet()) {
                paths.add(this.path + "." + key);
            }

            return paths;
        } else {
            return new ArrayList<>(this.data.keySet());
        }
    }

}
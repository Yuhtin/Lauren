package com.yuhtin.lauren.config;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class FileConfiguration {

    @Getter final File file;
    final Map<String, Object> data;

    public FileConfiguration(File file, Map<String, Object> data) {
        this.file = file;
        this.data = data;
    }

    public void reload() {
        try {
            FileReader fileReader = new FileReader(file);

            Yaml yaml = new Yaml();

            data.clear();

            Map<? extends String, ?> map = yaml.load(fileReader);
            data.putAll(map == null ? new HashMap<>() : map);
        } catch (Exception exception) {
            LoggerUtil.printException(exception);
        }
    }

    @SuppressWarnings("exception")
    @Nullable
    public <T> T get(String path) {
        String[] keys = path.split("\\.");
        Map<String, Object> currentMap = this.data;

        Object value = null;
        for (String key : keys) {
            value = currentMap.get(key);

            if (value instanceof Map) {
                currentMap = (Map<String, Object>) value;
            }
        }

        if (value == null) return null;

        try {
            return (T) value;
        } catch (NullPointerException | ClassCastException exception) {
            return null;
        }
    }

    @NotNull
    public String getString(String path, String def) {
        String string = get(path);
        return string != null ? string : def;
    }

    @Nullable
    public String getString(String path) {
        return getString(path, null);
    }

    public boolean getBoolean(String path, boolean def) {
        Boolean bool = get(path);
        return bool != null ? bool : def;
    }

    public boolean getBoolean(String path) {
        return getBoolean(path, false);
    }

    public Number getNumber(String target) {
        return getNumber(target, 0);
    }

    public Number getNumber(String target, Number def) {
        Object object = get(target);
        return object instanceof Number ? (Number) object : def;
    }

    @Nullable
    public List<String> getStringList(String path) {
        return get(path);
    }

    @Nullable
    public ConfigurationSection getConfigurationSection(String path) {
        if (path.isEmpty()) {
            return new ConfigurationSection(this, "");
        }

        Object value = get(path);
        if (value instanceof Map) {
            return new ConfigurationSection(this, path);
        }

        return null;
    }

    public Double getDouble(String path, double i) {
        if (path == null) return null;

        Object o = get(path);
        if (o == null) return i;

        if (o instanceof Double) {
            return (Double) o;
        }

        if (o instanceof Integer) {
            return ((Integer) o).doubleValue();
        }

        if (o instanceof Long) {
            return ((Long) o).doubleValue();
        }

        return getNumber(path, i).doubleValue();
    }
}
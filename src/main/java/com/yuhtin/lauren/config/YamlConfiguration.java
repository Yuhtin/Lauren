package com.yuhtin.lauren.config;

import com.yuhtin.lauren.util.EnvWrapper;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class YamlConfiguration extends FileConfiguration {

    private final Map<String, Object> modifiedData = new HashMap<>();

    public YamlConfiguration(File file, HashMap<String, Object> data) {
        super(file, data);
    }

    public static YamlConfiguration load(String fileName) {
        Logger logger = Logger.getLogger("Lauren");

        String branch = EnvWrapper.getBranch();

        File file = new File("config/" + branch + "/" + fileName);
        if (!file.exists()) {
            try {
                InputStream resource = YamlConfiguration.class.getResourceAsStream("/config/" + branch + "/" + fileName);
                if (resource != null) {
                    Files.copy(resource, Paths.get(file.toURI()), StandardCopyOption.REPLACE_EXISTING);
                    logger.config("Created " + fileName);
                } else {
                    if (file.createNewFile()) {
                        logger.config("Created " + fileName);
                    } else {
                        logger.severe("Failed to create " + fileName);
                    }
                }
            } catch (Exception exception) {
                logger.severe("Failed to create " + fileName);
                LoggerUtil.printException(exception);
            }
        }

        YamlConfiguration yamlConfiguration = new YamlConfiguration(file, new HashMap<>());
        yamlConfiguration.reload();

        return yamlConfiguration;
    }

    public void save() {
        try {
            HashMap<String, Object> savingData = new HashMap<>(this.data);
            savingData.putAll(modifiedData);

            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            options.setPrettyFlow(true);

            Yaml yaml = new Yaml(options);
            yaml.dump(savingData, new FileWriter(file));
        } catch (Exception exception) {
            LoggerUtil.printException(exception);
        }
    }


    public void set(String path, Object value) {
        String[] keys = path.split("\\.");
        Map<String, Object> currentMap = this.data;

        for (int i = 0; i < keys.length; i++) {
            String key = keys[i];
            Object current = currentMap.get(key);

            if (i == keys.length - 1) {
                currentMap.put(key, value);
                modifiedData.put(path, value);
                return;
            }

            if (current instanceof Map) {
                currentMap = (Map<String, Object>) current;
            } else {
                currentMap.put(key, new HashMap<String, Object>());
                currentMap = (Map<String, Object>) currentMap.get(key);
            }
        }
    }

    public void setString(String path, String value) {
        set(path, value);
    }

    public void setInt(String path, int value) {
        set(path, value);
    }

    public void setDouble(String path, double value) {
        set(path, value);
    }

    public void setLong(String path, long value) {
        set(path, value);
    }

    public void setBoolean(String path, boolean value) {
        set(path, value);
    }

    public void setStringList(String path, List<String> value) {
        set(path, value);
    }

}

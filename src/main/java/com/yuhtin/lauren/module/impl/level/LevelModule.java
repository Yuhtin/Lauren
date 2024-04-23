package com.yuhtin.lauren.module.impl.level;

import com.yuhtin.lauren.Lauren;
import com.yuhtin.lauren.config.YamlConfiguration;
import com.yuhtin.lauren.module.ConfigurableModule;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;

@Getter
public class LevelModule extends ConfigurableModule {

    private final HashMap<Integer, Level> levelByXp = new HashMap<>();

    @Override
    public boolean setup(Lauren lauren) {
        setConfig(YamlConfiguration.load("levels.yml"));
        return true;
    }

    @Override
    public void reload() {
        super.reload();
        load();
    }

    public void load() {
        levelByXp.clear();

        int experienceBase = 1280, experiencePerLevel = 232;

        int acumulated = 0;
        for (int level = 1; level <= 35; level++) {
            acumulated = acumulated + (level - 1) * experiencePerLevel;
            int experience = experienceBase + acumulated;

            Level levelObject = new Level(level, experience);

            List<String> rewards = getConfig().getStringList("levels." + level);
            if (rewards != null) {
                rewards.forEach(role -> levelObject.getRolesToGive().add(Long.parseLong(role)));
            }

            levelByXp.put(level, levelObject);
        }

        levelByXp.put(0, new Level(0, 0));
    }

    public boolean canUpgrade(int nextLevel, int experience) {
        if (!this.levelByXp.containsKey(nextLevel)) return false;
        return this.levelByXp.get(nextLevel).getMiniumExperience() <= experience;
    }

    public Level getLevel(int level) {
        return levelByXp.get(level);
    }

    @Nullable
    public Level getLevelByExperience(int experience) {
        for (Level level : levelByXp.values()) {
            if (level.getMiniumExperience() > experience) {
                return levelByXp.get(level.getLevel() - 1);
            }
        }

        return null;
    }

}

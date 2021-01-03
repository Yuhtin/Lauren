package com.yuhtin.lauren.core.xp;

import com.yuhtin.lauren.sql.dao.ExperienceDAO;
import lombok.Getter;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
public class XpController {

    @Getter private final Map<Integer, Level> levelByXp = new HashMap<>();

    @Inject private ExperienceDAO experienceDAO;

    public void load() {

        // load rewards
        this.experienceDAO.createTable();

        // level, rewards (role id's)
        List<Level> levels = experienceDAO.findAllLevel();
        Map<Integer, List<Long>> levelRewards = new HashMap<>();

        for (Level level : levels) levelRewards.put(level.getLevel(), level.getRolesToGive());

        // cumulative exponential multiplication

        int experienceBase = 1280, experiencePerLevel = 232;

        int acumulated = 0;
        for (int level = 1; level <= 35; level++) {
            acumulated = acumulated + (level - 1) * experiencePerLevel;
            int experience = experienceBase + acumulated;

            Level levelBuild = Level.builder()
                    .miniumExperience(experience)
                    .build();

            levelBuild.getRolesToGive().addAll(levelRewards.getOrDefault(level, new ArrayList<>()));
            levelByXp.put(level, levelBuild);

        }
    }

    public boolean canUpgrade(int nextLevel, int experience) {

        if (!this.levelByXp.containsKey(nextLevel)) return false;
        return this.levelByXp.get(nextLevel).getMiniumExperience() <= experience;

    }

}

package com.yuhtin.lauren.core.xp;

import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.database.DatabaseController;
import lombok.Getter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XpController {

    private static XpController instance;
    @Getter private final Map<Integer, Level> levelByXp = new HashMap<>();

    public XpController() {

        // load rewards
        createTable();

        // level, rewards (role id's)
        Map<Integer, List<Long>> levelWithRewards = loadTable();

        // cumulative exponential multiplication

        int experienceBase = 1280, experiencePerLevel = 232;

        int acumulated = 0;
        for (int level = 1; level <= 35; level++) {
            acumulated = acumulated + (level - 1) * experiencePerLevel;
            int experience = experienceBase + acumulated;

            Level levelBuild = Level.builder()
                    .miniumExperience(experience)
                    .build();

            levelBuild.getRolesToGive().addAll(levelWithRewards.getOrDefault(level, new ArrayList<>()));
            levelByXp.put(level, levelBuild);

        }
    }

    public static XpController getInstance() {

        if (instance == null) instance = new XpController();
        return instance;

    }

    public boolean canUpgrade(int nextLevel, int experience) {
        if (!this.levelByXp.containsKey(nextLevel)) return false;

        return this.levelByXp.get(nextLevel).getMiniumExperience() <= experience;
    }


    private void createTable() {
        DatabaseController.getDatabase()
                .updateSync(
                        "create table if not exists `lauren_levelrewards` " +
                                "(" +
                                "`level` int(3) primary key not null, " +
                                "`rewards` text" +
                                ");");
    }

    private HashMap<Integer, List<Long>> loadTable() {
        HashMap<Integer, List<Long>> map = new HashMap<>();

        String sql = "select * from `lauren_levelrewards`";
        try (PreparedStatement statement = DatabaseController.get().getConnection().prepareStatement(sql)) {

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {

                List<Long> rewards = new ArrayList<>();
                String list = resultSet.getString("rewards");

                if (list.equalsIgnoreCase("")) {
                    map.put(resultSet.getInt("level"), rewards);
                    continue;
                }

                if (!list.contains(",")) rewards.add(Long.parseLong(list));
                else {

                    String[] rewardsString = list.split(",");
                    for (String string : rewardsString) rewards.add(Long.parseLong(string));

                }

                map.put(resultSet.getInt("level"), rewards);

            }

            resultSet.close();
            statement.close();

            return map;
        } catch (Exception exception) {
            Logger.error(exception);
        }

        return map;
    }
}

package com.yuhtin.lauren.sql.provider.document.parser.impl;

import com.yuhtin.lauren.core.xp.Level;
import com.yuhtin.lauren.sql.provider.document.Document;
import com.yuhtin.lauren.sql.provider.document.parser.DocumentParser;
import lombok.Getter;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
public class ExperienceDocumentParser implements DocumentParser<Level> {

    @Getter private static final ExperienceDocumentParser instance = new ExperienceDocumentParser();

    @Override
    public Level parse(Document document) {

        Level level = Level.builder()
                .miniumExperience(document.getNumber("level").intValue())
                .build();

        String rewards = document.getString("rewards");
        if (!rewards.equalsIgnoreCase("")) {

            if (!rewards.contains(",")) level.getRolesToGive().add(Long.parseLong(rewards));
            else {

                String[] rewardsString = rewards.split(",");
                for (String string : rewardsString) level.getRolesToGive().add(Long.parseLong(string));

            }

        }


        return level;

    }
}

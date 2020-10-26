package com.yuhtin.lauren.connectiontest;

import com.yuhtin.lauren.core.logger.Logger;

public class CalcTest {

    public static void main(String[] args) {
        int experienceBase = 1280, experiencePerLevel = 232;

        int acumulated = 0;
        for (int level = 1; level <= 35; level++) {

            acumulated = acumulated + (level - 1) * experiencePerLevel;
            int experience = experienceBase + acumulated;

            Logger.log("Caching level " + level + " with " + experience + "xp");
        }
    }
}

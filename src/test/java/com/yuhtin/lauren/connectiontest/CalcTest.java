package com.yuhtin.lauren.connectiontest;

public class CalcTest {

    public static void main(String[] args) {
        int experienceBase = 1280, experiencePerLevel = 232;

        int acumulated = 0;
        for (int level = 1; level <= 35; level++) {

            acumulated = acumulated + (level - 1) * experiencePerLevel;
            int experience = experienceBase + acumulated;

            System.out.println(level + "> " + experience + "xp");
        }
    }
}

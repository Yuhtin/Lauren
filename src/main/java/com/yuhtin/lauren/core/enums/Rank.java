package com.yuhtin.lauren.core.enums;

import lombok.AllArgsConstructor;

import java.util.Arrays;

@AllArgsConstructor
public enum Rank {

    NOTHING("Sem patente", 0, 0, 1, "https://i.imgur.com/m8KIawu.png"),

    SILVERI("Prata I", 1, 4, 1, "https://csgo-stats.com/custom/img/ranks/1.png"),
    SILVERII("Prata II", 2, 12, 1, "https://csgo-stats.com/custom/img/ranks/2.png"),
    SILVERIII("Prata III", 3, 20, 1, "https://csgo-stats.com/custom/img/ranks/3.png"),
    SILVERIV("Prata IV", 4, 25, 1, "https://csgo-stats.com/custom/img/ranks/4.png"),
    SILVERELITE("Prata Elite", 5, 32, 1.05, "https://csgo-stats.com/custom/img/ranks/5.png"),
    SILVERMASTER("Prata Elite Mestre", 6, 40, 1.1, "https://csgo-stats.com/custom/img/ranks/6.png"),

    GOLDI("Ouro I", 7, 45, 1.25, "https://csgo-stats.com/custom/img/ranks/7.png"),
    GOLDII("Ouro II", 8, 49, 1.30, "https://csgo-stats.com/custom/img/ranks/8.png"),
    GOLDIII("Ouro III", 9, 54, 1.35, "https://csgo-stats.com/custom/img/ranks/9.png"),
    GOLDIV("Ouro IV", 10, 59, 1.40, "https://csgo-stats.com/custom/img/ranks/10.png"),
    AKI("AK I", 11, 70, 1.55, "https://csgo-stats.com/custom/img/ranks/11.png"),
    AKII("AK II", 12, 75, 1.60, "https://csgo-stats.com/custom/img/ranks/12.png"),

    AKX("AK X", 13, 79, 1.65, "https://csgo-stats.com/custom/img/ranks/13.png"),
    SHERIFF("Xerife", 14, 85, 1.70, "https://csgo-stats.com/custom/img/ranks/14.png"),
    EAGLEI("Águia I", 15, 89, 1.90, "https://csgo-stats.com/custom/img/ranks/15.png"),
    EAGLEII("Águia II", 16, 94, 1.95, "https://csgo-stats.com/custom/img/ranks/16.png"),
    SUPREME("Supremo", 17, 100, 2, "https://csgo-stats.com/custom/img/ranks/17.png"),
    GLOBAL("Global", 18, 120, 2.3, "https://csgo-stats.com/custom/img/ranks/18.png");

    public final String name;
    public final int position, minimumPoints;
    public final double multiplier;
    public final String url;

    public static Rank getByPosition(int position) {
        return Arrays.stream(Rank.values()).filter(rank -> rank.position == position).findFirst().orElse(Rank.NOTHING);
    }

    public static Rank getByPoints(int experience) {
        Rank rank = Rank.NOTHING;
        for (Rank value : Rank.values()) {
            if (value.minimumPoints <= experience)
                rank = value;
        }

        return rank;
    }

    @Override
    public String toString() {
        return name;
    }
}

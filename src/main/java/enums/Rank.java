package enums;

import lombok.AllArgsConstructor;

import java.util.Arrays;

@AllArgsConstructor
public enum Rank {

    NOTHING("Sem patente", 0, 0, 1),

    SILVERI("Prata I", 1, 100, 1),
    SILVERII("Prata II", 2, 500, 1),
    SILVERIII("Prata III", 3, 750, 1),
    SILVERIV("Prata IV", 4, 1300, 1),
    SILVERELITE("Prata Elite", 5, 1500, 1.05),
    SILVERMASTER("Prata Elite Mestre", 6, 1750, 1.1),

    GOLDI("Gold I", 7, 2000, 1.25),
    GOLDII("Gold II", 8, 2300, 1.30),
    GOLDIII("Gold III", 9, 3000, 1.35),
    GOLDIV("Gold IV", 10, 3500, 1.40),
    AKI("AK I", 11, 4000, 1.55),
    AKII("AK II", 12, 5000, 1.60),

    AKX("AK X", 13, 6000, 1.65),
    SHERIFF("Xerife", 14, 6500, 1.70),
    EAGLEI("Aguia I", 15, 7300, 1.90),
    EAGLEII("Aguia II", 16, 8000, 1.95),
    SUPREME("Supremo", 17, 9500, 2),
    GLOBAL("Global", 18, 12000, 2.3);


    public final String name;
    public final int position, minimumPoints;
    public final double multiplier;

    public static Rank getByPosition(int position) {
        return Arrays.stream(Rank.values()).filter(rank -> rank.position == position).findFirst().orElse(Rank.NOTHING);
    }

    public static Rank getByPoints(int experience) {
        Rank rank = Rank.NOTHING;
        for (Rank value : Rank.values()) {
            if (value.minimumPoints <= experience) rank = value;
        }

        return rank;
    }
}

package enums;

import lombok.AllArgsConstructor;

import java.util.Arrays;

@AllArgsConstructor
public enum Rank {

    NOTHING("Sem patente", 0, 0, 1),

    SILVERI("Prata I", 1, 4, 1),
    SILVERII("Prata II", 2, 12, 1),
    SILVERIII("Prata III", 3, 20, 1),
    SILVERIV("Prata IV", 4, 25, 1),
    SILVERELITE("Prata Elite", 5, 32, 1.05),
    SILVERMASTER("Prata Elite Mestre", 6, 40, 1.1),

    GOLDI("Ouro I", 7, 45, 1.25),
    GOLDII("Ouro II", 8, 49, 1.30),
    GOLDIII("Ouro III", 9, 54, 1.35),
    GOLDIV("Ouro IV", 10, 59, 1.40),
    AKI("AK I", 11, 70, 1.55),
    AKII("AK II", 12, 75, 1.60),

    AKX("AK X", 13, 79, 1.65),
    SHERIFF("Xerife", 14, 85, 1.70),
    EAGLEI("Aguia I", 15, 89, 1.90),
    EAGLEII("Aguia II", 16, 94, 1.95),
    SUPREME("Supremo", 17, 100, 2),
    GLOBAL("Global", 18, 120, 2.3);

    public final String name;
    public final int position, minimumPoints;
    public final double multiplier;

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

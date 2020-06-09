package enums;

import lombok.AllArgsConstructor;

import javax.annotation.Nullable;
import java.util.Arrays;

@AllArgsConstructor
public enum Rank {

    NOTHING("Sem patente", 0, 0),

    SILVERI("Prata I", 1, 100),
    SILVERII("Prata II", 2, 500),
    SILVERIII("Prata III", 3, 750),
    SILVERIV("Prata IV", 4, 1300),
    SILVERELITE("Prata Elite", 5, 1500),
    SILVERMASTER("Prata Elite Mestre", 6, 1750),

    GOLDI("Gold I", 7, 2000),
    GOLDII("Gold II", 8, 2300),
    GOLDIII("Gold III", 9, 3000),
    GOLDIV("Gold IV", 10, 3500),
    AKI("AK I", 11, 4000),
    AKII("AK II", 12, 5000),

    AKX("AK X", 13, 6000),
    SHERIFF("Xerife", 14, 6500),
    EAGLEI("Aguia I", 15, 7300),
    EAGLEII("Aguia II", 16, 8000),
    SUPREME("Supremo", 17, 9500),
    GLOBAL("Global", 18, 12000);


    private final String name;
    private final int position, experience;

    public static Rank getByPosition(int position) {
        return Arrays.stream(Rank.values()).filter(rank -> rank.position == position).findFirst().orElse(Rank.NOTHING);
    }

    public static Rank getByExperience(int experience) {
        Rank rank = Rank.NOTHING;
        for (Rank value : Rank.values()) {
            if (value.experience <= experience) rank = value;
        }

        return rank;
    }
}

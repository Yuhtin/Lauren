package enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Game {

    POOL("8BallPool"),
    LUDO("LudoKing");

    public final String name;

    @Override
    public String toString() {
        return name;
    }
}

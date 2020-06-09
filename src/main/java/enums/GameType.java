package enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum GameType {

    BALL("8BallPool"),
    LUDO("LudoKing");

    public final String name;

}

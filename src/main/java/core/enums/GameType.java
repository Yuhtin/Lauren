package core.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum GameType {

    CASUAL("Casual"),
    RANKED("Ranked");

    private final String name;

    @Override
    public String toString() {
        return name;
    }
}

package com.yuhtin.lauren.core.player;

import com.yuhtin.lauren.models.enums.Rank;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class OldPlayer {

    private final List<String> alarmsName;
    private final List<String> winMatches;

    // Geral
    private long userID, dailyDelay;
    private int level = 0, money = 100, experience = 0;

    // Valorant and 8ballpool variables
    private Rank valorantRank = Rank.NOTHING,
            poolRank = Rank.NOTHING;
    private int valorantPoints, valorantWins, valorantMatches,
            poolPoints, poolWins, poolMatches = 0;

    public OldPlayer(long userID) {
        this.userID = userID;
        winMatches = new ArrayList<>();
        alarmsName = new ArrayList<>();
    }
}

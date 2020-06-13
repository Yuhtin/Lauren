package models.data;

import application.Lauren;
import enums.GameType;
import enums.Rank;
import utils.helper.Utilities;

import java.util.ArrayList;
import java.util.List;

public class PlayerData {

    // Geral
    public Long userID;
    public int level = 0;
    public double money = 100, experience = 0;

    // LudoKing and 8ballpool variables
    public Rank ludoRank = Rank.NOTHING,
            poolRank = Rank.NOTHING;
    public int ludoPoints, ludoWins, ludoMatches,
            poolPoints, poolWins, poolMatches = 0;

    public final List<Match> winMatches;

    public PlayerData(Long userID) {
        this.userID = userID;
        winMatches = new ArrayList<>();
    }

    public PlayerData updateLevel() {
        level = (int) experience / 1000;
        Utilities.setNick(userID, level);

        return this;
    }

    public PlayerData updateRank() {
        this.poolRank = Rank.getByPoints(poolPoints);
        this.ludoRank = Rank.getByPoints(ludoPoints);

        return this;
    }

    public PlayerData gainXP(double quantity) {
        experience += (quantity * (poolRank.multiplier + ludoRank.multiplier));

        return this;
    }

    public PlayerData computMatch(Match match) {
        int points = -1;
        double experience = 350;
        double money = 0;
        double multiplier;
        boolean win = false;

        if (match.winPlayer.equals(userID)) {
            win = true;
            winMatches.add(match);
            points = 3;
            experience += 200;
            money += 35;
            winMatches.add(match);
        }

        if (match.type == GameType.BALL) {
            multiplier = poolRank.multiplier;
            ++poolMatches;
            poolPoints += points;
            if (win) ++poolWins;
        } else {
            multiplier = ludoRank.multiplier;
            ++ludoMatches;
            ludoPoints += points;
            if (win) ++ludoWins;
        }

        experience *= multiplier;
        this.experience += experience;
        this.money += money;

        updateRank();
        updateLevel();
        return this;
    }

    public void save() {
        Lauren.data.save(userID, this);
    }
}

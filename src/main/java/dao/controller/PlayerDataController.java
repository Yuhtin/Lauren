package dao.controller;

import enums.Rank;
import utils.helper.Utilities;

public class PlayerDataController {

    // Geral
    public Long userID;
    public int level;
    public double money = 100, experience;

    // LudoKing and 8ballpool variables
    public Rank ludoRank,
            poolRank = Rank.NOTHING;
    public int ludoPoints, ludoWins, ludoMatches,
            poolPoints, poolWins, poolMatches;

    public PlayerDataController(Long userID) {
        this.userID = userID;
    }

    public void updateLevel() {
        level = (int) experience / 1000;
        Utilities.setNick(userID, level);
    }

    public void updateRank() {
        this.poolRank = Rank.getByPoints(poolPoints);
        this.ludoRank = Rank.getByPoints(ludoPoints);
    }

    public void gainXP(double quantity) {
        experience += (quantity * (poolRank.multiplier + ludoRank.multiplier));
    }
}

package dao.controller;

import enums.Rank;

public class PlayerDataController {

    public Long userID;

    public Rank rank;
    public double money;
    public int experience, ludoWins, poolWins, ludoMatches, poolMatches;

    public PlayerDataController(Long userID) {
        this.userID = userID;
        this.experience = 0;
        this.rank = Rank.NOTHING;
    }

    public void updateRank() { this.rank = Rank.getByExperience(experience); }

}

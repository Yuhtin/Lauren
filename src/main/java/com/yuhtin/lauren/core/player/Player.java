package com.yuhtin.lauren.core.player;

import com.yuhtin.lauren.application.Lauren;
import com.yuhtin.lauren.core.match.Match;
import com.yuhtin.lauren.models.enums.GameMode;
import com.yuhtin.lauren.models.enums.GameType;
import com.yuhtin.lauren.models.enums.Rank;
import com.yuhtin.lauren.utils.helper.Utilities;

import java.util.ArrayList;
import java.util.List;

public class Player {

    public final List<String> winMatches;
    // Geral
    public long userID, dailyDelay;
    public int level = 0, money = 100, experience = 0;
    // LudoKing and 8ballpool variables
    public Rank ludoRank = Rank.NOTHING,
            poolRank = Rank.NOTHING;
    public int ludoPoints, ludoWins, ludoMatches,
            poolPoints, poolWins, poolMatches = 0;

    public Player(long userID) {
        this.userID = userID;
        winMatches = new ArrayList<>();
    }

    public Player updateLevel() {
        int result = experience / 1000;
        if (result != level) {
            level = result;
            new Thread(() -> Utilities.updateNickByLevel(userID, level)).start();
            if (level == 21) {
                Lauren.guild.addRoleToMember(userID, Lauren.guild.getRoleById(722091214400258068L)).queue();
                Lauren.guild.getTextChannelById(700683423429165096L)
                        .sendMessage("<:prime:722115525232296056> O jogador " + Utilities.getFullName(Lauren.bot.getUserById(userID)) + " se tornou prime").queue();
            }
        }

        return this;
    }

    public Player updateRank() {
        this.poolRank = Rank.getByPoints(poolPoints);
        this.ludoRank = Rank.getByPoints(ludoPoints);

        return this;
    }

    public Player setDelay(long delay) {
        this.dailyDelay = delay;

        return this;
    }

    public Player addMoney(double quantity) {
        money += quantity;

        return this;
    }

    public Player removeMoney(double quantity) {
        money -= quantity;

        return this;
    }

    public Player gainXP(double quantity) {
        experience += (quantity * (poolRank.multiplier + ludoRank.multiplier));

        return this;
    }

    public Player computMatch(Match match) {
        int points = -1;
        double experience = 350;
        double money = 0;
        double multiplier;
        boolean win = false;

        if (match.winPlayer.equals(userID)) {
            win = true;
            winMatches.add(match.id);
            points = 3;
            experience += 200;
            money += 35;
        }
        if (match.game.mode == GameMode.RANKED) points = 0;

        if (match.game.type == GameType.POOL) {
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
        if (money < 0) money = 0;
        if (ludoPoints < 0) ludoPoints = 0;
        if (poolPoints < 0) poolPoints = 0;

        Lauren.data.save(userID, this);
    }
}

package com.yuhtin.lauren.core.player;

import com.yuhtin.lauren.Lauren;
import com.yuhtin.lauren.core.alarm.Alarm;
import com.yuhtin.lauren.core.match.Match;
import com.yuhtin.lauren.core.player.controller.PlayerDatabase;
import com.yuhtin.lauren.core.statistics.controller.StatsController;
import com.yuhtin.lauren.models.enums.GameMode;
import com.yuhtin.lauren.models.enums.GameType;
import com.yuhtin.lauren.models.enums.Rank;
import com.yuhtin.lauren.utils.helper.Utilities;
import net.dv8tion.jda.api.entities.Member;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Player {

    public transient final List<Alarm> alarms = new ArrayList<>();
    public final List<String> alarmsName;
    public final List<String> winMatches;

    // Geral
    public long userID, dailyDelay;
    public int level = 0, money = 100, experience = 0;

    // Valorant and 8ballpool variables
    public Rank valorantRank = Rank.NOTHING,
            poolRank = Rank.NOTHING;
    public int valorantPoints, valorantWins, valorantMatches,
            poolPoints, poolWins, poolMatches = 0;

    public Player(long userID) {
        this.userID = userID;
        winMatches = new ArrayList<>();
        alarmsName = new ArrayList<>();
    }

    public Player updateLevel() {
        int result = experience / 1000;
        if (result != level) {
            level = result;
            new Thread(() -> Utilities.INSTANCE.updateNickByLevel(userID, level)).start();
            if (level == 21) {
                Lauren.guild.addRoleToMember(userID, Lauren.guild.getRoleById(722116789055782912L)).queue();
                Lauren.guild.getTextChannelById(700683423429165096L).sendMessage(
                        "<:prime:722115525232296056> O jogador <@" + userID + "> tornou-se prime").queue();
            }

            StatsController.get().getStats("Evoluir Nível").suplyStats(1);
        }

        return this;
    }

    public Player updateRank() {
        this.poolRank = Rank.getByPoints(poolPoints);
        this.valorantRank = Rank.getByPoints(valorantPoints);

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
        List<Double> multipliers = Arrays.asList(boosterMultiplier(), poolRank.multiplier, valorantRank.multiplier);

        for (Double multiplier : multipliers) quantity *= multiplier;
        experience += quantity;

        StatsController.get().getStats("Ganhar XP").suplyStats(1);
        return this;
    }

    private double boosterMultiplier() {
        Member member = Lauren.guild.getMemberById(userID);

        return Utilities.INSTANCE.isBooster(member) ? 1.25 : Utilities.INSTANCE.isPrime(member) ? 1.15 : 1;
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
            multiplier = valorantRank.multiplier;
            ++valorantMatches;
            valorantPoints += points;
            if (win) ++valorantWins;
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
        if (valorantPoints < 0) valorantPoints = 0;
        if (poolPoints < 0) poolPoints = 0;

        PlayerDatabase.save(userID, this);
    }
}

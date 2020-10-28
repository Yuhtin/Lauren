package com.yuhtin.lauren.core.player;

import com.yuhtin.lauren.Lauren;
import com.yuhtin.lauren.core.alarm.Alarm;
import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.core.match.Match;
import com.yuhtin.lauren.core.player.controller.PlayerDatabase;
import com.yuhtin.lauren.core.statistics.controller.StatsController;
import com.yuhtin.lauren.core.xp.Level;
import com.yuhtin.lauren.core.xp.XpController;
import com.yuhtin.lauren.models.enums.GameMode;
import com.yuhtin.lauren.models.enums.GameType;
import com.yuhtin.lauren.models.enums.LogType;
import com.yuhtin.lauren.models.enums.Rank;
import com.yuhtin.lauren.utils.helper.Utilities;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

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

    public void updateLevel(int level) {
        this.level = level;
        new Thread(() -> Utilities.INSTANCE.updateNickByLevel(userID, level)).start();

        List<Long> rolesToGive = XpController.getInstance()
                .getLevelByXp()
                .get(level)
                .getRolesToGive();

        List<Long> rolesToRemove = new ArrayList<>();
        if (level >= 10) {
            for (Integer integer : XpController.getInstance().getLevelByXp().keySet()) {
                if (integer >= level) break;

                Level tempLevel = XpController.getInstance().getLevelByXp().get(integer);
                if (tempLevel.getRolesToGive().isEmpty()) continue;

                rolesToRemove.addAll(tempLevel.getRolesToGive());
            }

        }

        if (rolesToGive != null && !rolesToGive.isEmpty()) {

            for (long roleID : rolesToGive) {

                Role role = Lauren.guild.getRoleById(roleID);
                if (role == null) {
                    Logger.log("Role is null", LogType.ERROR);
                    continue;
                }

                Lauren.guild.addRoleToMember(userID, role).queue();

            }

            for (long roleID : rolesToRemove) {

                Role role = Lauren.guild.getRoleById(roleID);
                if (role == null) {
                    Logger.log("Role is null", LogType.ERROR);
                    continue;
                }

                Lauren.guild.removeRoleFromMember(userID, role).queue();

            }
        }

        String message = "Parabéns <@" + userID + "> você alcançou o nível **__" + level + "__** <a:tutut:770408915300384798>";

        if (level == 20) message = "<:prime:722115525232296056> O jogador <@" + userID + "> tornou-se Prime";
        if (level == 30) message = "<:oi:762303876732420176> O jogador <@" + userID + "> tornou-se DJ";

        TextChannel channel = Lauren.bot.getTextChannelById(770393139516932158L);
        if (channel != null) channel.sendMessage(message).queue();

        Utilities.INSTANCE.updateNickByLevel(userID, level);

        StatsController.get().getStats("Evoluir Nível").suplyStats(1);
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
        experience += (int) quantity;

        int nextLevel = level + 1;
        if (XpController.getInstance().canUpgrade(nextLevel, experience)) updateLevel(nextLevel);

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
        return this;
    }

    public void save() {
        if (money < 0) money = 0;
        if (valorantPoints < 0) valorantPoints = 0;
        if (poolPoints < 0) poolPoints = 0;

        PlayerDatabase.save(userID, this);
    }
}

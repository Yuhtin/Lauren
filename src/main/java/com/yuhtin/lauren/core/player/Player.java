package com.yuhtin.lauren.core.player;

import com.yuhtin.lauren.Lauren;
import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.core.player.controller.PlayerDatabase;
import com.yuhtin.lauren.core.punish.PunishmentType;
import com.yuhtin.lauren.core.statistics.controller.StatsController;
import com.yuhtin.lauren.core.xp.Level;
import com.yuhtin.lauren.core.xp.XpController;
import com.yuhtin.lauren.models.enums.LogType;
import com.yuhtin.lauren.models.enums.Rank;
import com.yuhtin.lauren.models.objects.Entity;
import com.yuhtin.lauren.utils.helper.Utilities;
import lombok.Data;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

import java.io.Serializable;
import java.util.*;

@Data
public class Player
        extends Entity
        implements Serializable {

    private Map<PunishmentType, Long> punishs = new HashMap<>();

    // Geral
    private long userID;
    private long dailyDelay;
    private long leaveTime;
    private int level = 0;
    private int money = 100;
    private int experience = 0;
    private int lootBoxes = 0;
    private int rankedPoints = 0;
    private int totalEvents = 0;
    private int keys = 0;

    private Rank rank = Rank.NOTHING;

    public Player(long userID) {
        this.userID = userID;
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

                for (Long roleID : tempLevel.getRolesToGive()) {

                    if (roleID.equals(722957999949348935L)
                            || roleID.equals(722116789055782912L)
                            || roleID.equals(770371418177011713L)) continue;

                    rolesToRemove.add(roleID);
                }

            }

        }

        if (rolesToGive != null && !rolesToGive.isEmpty()) {

            for (long roleID : rolesToGive) {

                Role role = Lauren.getInstance().getGuild().getRoleById(roleID);
                if (role == null) {
                    Logger.log("Role is null", LogType.ERROR);
                    continue;
                }

                Lauren.getInstance().getGuild().addRoleToMember(userID, role).queue();

            }

            for (long roleID : rolesToRemove) {

                Role role = Lauren.getInstance().getGuild().getRoleById(roleID);
                if (role == null) {
                    Logger.log("Role is null", LogType.ERROR);
                    continue;
                }

                Lauren.getInstance().getGuild().removeRoleFromMember(userID, role).queue();

            }
        }

        String message = "Parabéns <@" + userID + "> você alcançou o nível **__" + level + "__** <a:tutut:770408915300384798>";

        if (level == 20) message = "<:prime:722115525232296056> O jogador <@" + userID + "> tornou-se Prime";
        if (level == 30) message = "<:oi:762303876732420176> O jogador <@" + userID + "> tornou-se DJ";

        TextChannel channel = Lauren.getInstance().getBot().getTextChannelById(770393139516932158L);
        if (channel != null) channel.sendMessage(message).queue();

        Utilities.INSTANCE.updateNickByLevel(userID, level);

        StatsController.get().getStats("Evoluir Nível").suplyStats(1);
    }


    public Player updateRank() {
        this.rank = Rank.getByPoints(rankedPoints);

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
        List<Double> multipliers = Arrays.asList(boosterMultiplier(), rank.getMultiplier());

        for (Double multiplier : multipliers) quantity *= multiplier;
        experience += (int) quantity;

        int nextLevel = level + 1;
        if (XpController.getInstance().canUpgrade(nextLevel, experience)) updateLevel(nextLevel);

        StatsController.get().getStats("Ganhar XP").suplyStats(1);
        return this;
    }

    private double boosterMultiplier() {
        Member member = Lauren.getInstance().getGuild().getMemberById(userID);

        double primeBooster = Utilities.INSTANCE.isPrime(member) ? 1.15 : 1;
        return Utilities.INSTANCE.isBooster(member) ? 1.25 : primeBooster;
    }

    public void save() {
        if (money < 0) money = 0;
        if (rankedPoints < 0) rankedPoints = 0;

        PlayerDatabase.save(userID, this);
    }
}

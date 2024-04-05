package com.yuhtin.lauren.core.player;

import com.google.inject.Inject;
import com.yuhtin.lauren.Lauren;
import com.yuhtin.lauren.Startup;
import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.core.player.impl.Entity;
import com.yuhtin.lauren.core.punish.PunishmentType;
import com.yuhtin.lauren.core.statistics.StatsController;
import com.yuhtin.lauren.core.xp.XpController;
import com.yuhtin.lauren.module.impl.player.Rank;
import com.yuhtin.lauren.startup.Startup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.val;
import net.dv8tion.jda.api.entities.Guild;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Data
@EqualsAndHashCode(callSuper = true)
public class Player
        extends Entity
        implements Serializable {

    private Map<PunishmentType, Long> punishs = new HashMap<>();

    // Geral
    private long userID;
    private long voteDelay;
    private long leaveTime;
    private int votes = 0;
    private int level = 0;
    private int money = 100;
    private int lootBoxes = 0;
    private int totalEvents = 0;
    private int keys = 0;
    private int rankedPoints = 0;

    private boolean hideLevelOnNickname = false;

    private transient int experience = 0;
    private transient boolean abbleToDaily = true;

    private Rank rank = Rank.NONE;

    public Player(long userID) {
        this.userID = userID;
    }

    public void updateLevel(int level) {

        this.level = level;

        val rolesToGive = xpController
                .getLevelByXp()
                .get(level)
                .getRolesToGive();

        val rolesToRemove = new ArrayList<Long>();
        for (val integer : xpController.getLevelByXp().keySet()) {
            if (integer >= level) break;

            val tempLevel = xpController.getLevelByXp().get(integer);
            if (tempLevel.getRolesToGive().isEmpty()) continue;

            for (val roleID : tempLevel.getRolesToGive()) {

                if (roleID.equals(722957999949348935L)
                        || roleID.equals(722116789055782912L)
                        || roleID.equals(770371418177011713L)) continue;

                rolesToRemove.add(roleID);
            }

        }

        Lauren lauren = Startup.getLauren();
        Guild guild = lauren.getGuild();

        if (rolesToGive != null && !rolesToGive.isEmpty()) {
            for (val roleID : rolesToGive) {
                val role = guild.getRoleById(roleID);
                if (role == null) {
                    logger.warning("Role is null");
                    continue;
                }

                guild.addRoleToMember(userID, role).queue();

            }

            for (val roleID : rolesToRemove) {

                val role = guild.getRoleById(roleID);
                if (role == null) {

                    logger.warning("Role is null");
                    continue;

                }

                guild.removeRoleFromMember(userID, role).queue();

            }
        }

        var message = "Parabéns <@" + userID + "> você alcançou o nível **__" + level + "__** <a:tutut:770408915300384798>";

        if (level == 20) {

            message += "\n<:prime:722115525232296056> O jogador <@" + userID + "> tornou-se Prime";
            addPermission("role.prime");

        }

        val channel = guild.getTextChannelById(770393139516932158L);
        if (channel != null) {

            channel.sendMessage(message).queue();
            if (level == 30)
                channel.sendMessage("<:oi:762303876732420176> O jogador <@" + userID + "> tornou-se DJ").queue();

        }

        UserUtil.updateNickByLevel(this, level);
        statsController.getStats("Evoluir Nível").suplyStats(1);

    }


    public Player updateRank() {

        rank = Rank.getByPoints(rankedPoints);
        return this;

    }

    public Player addMoney(double quantity) {
        val multiplier = multiply();

        quantity *= multiplier;
        money += quantity;

        return this;
    }

    public Player setAbbleToDaily(boolean abbleToDaily) {

        this.abbleToDaily = abbleToDaily;
        return this;

    }

    public Player removeMoney(double quantity) {

        money -= quantity;
        return this;

    }

    public Player gainXP(double quantity) {

        val multiplier = multiply();

        quantity *= multiplier;
        experience += quantity;

        val nextLevel = level + 1;
        if (xpController.canUpgrade(nextLevel, experience)) updateLevel(nextLevel);

        statsController.getStats("Ganhar XP").suplyStats(1);

        return this;
    }

    public void executeVote() {

        abbleToDaily = true;
        voteDelay = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(12);
        gainXP(250);
        addMoney(75);
        setRankedPoints(this.getRankedPoints() + 2);
        ++votes;

    }

    @Override
    public double multiply() {

        assertList();
        return 1 + getPermissions().stream().filter(multiplierList::containsKey).mapToDouble(multiplierList::get).sum();

    }

    @Override
    public String toString() {
        return "Player{" +
                "punishs=" + punishs +
                ", userID=" + userID +
                ", voteDelay=" + voteDelay +
                ", leaveTime=" + leaveTime +
                ", votes=" + votes +
                ", level=" + level +
                ", money=" + money +
                ", experience=" + experience +
                ", lootBoxes=" + lootBoxes +
                ", rankedPoints=" + rankedPoints +
                ", totalEvents=" + totalEvents +
                ", keys=" + keys +
                ", hideLevelOnNickname=" + hideLevelOnNickname +
                ", abbleToDaily=" + abbleToDaily +
                ", rank=" + rank +
                "} " + super.toString();
    }
}


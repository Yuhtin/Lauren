package com.yuhtin.lauren.core.player;

import com.google.inject.Inject;
import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.core.player.impl.Entity;
import com.yuhtin.lauren.core.punish.PunishmentType;
import com.yuhtin.lauren.core.statistics.StatsController;
import com.yuhtin.lauren.core.xp.Level;
import com.yuhtin.lauren.core.xp.XpController;
import com.yuhtin.lauren.models.enums.Rank;
import com.yuhtin.lauren.startup.Startup;
import com.yuhtin.lauren.utils.helper.UserUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Data
@EqualsAndHashCode(callSuper = true)
public class Player
        extends Entity
        implements Serializable {

    @Inject private static Logger logger;
    @Inject private static StatsController statsController;
    @Inject private static XpController xpController;

    private Map<PunishmentType, Long> punishs = new HashMap<>();

    // Geral
    private long userID;
    private long voteDelay;
    private long leaveTime;
    private int votes = 0;
    private int level = 0;
    private int money = 100;
    private int lootBoxes = 0;
    private int rankedPoints = 0;
    private int totalEvents = 0;
    private int keys = 0;

    private boolean hideLevelOnNickname = false;

    private transient int experience = 0;
    private transient boolean abbleToDaily = true;

    private Rank rank = Rank.NOTHING;

    public Player(long userID) {
        this.userID = userID;
    }

    public void updateLevel(int level) {

        this.level = level;

        List<Long> rolesToGive = xpController
                .getLevelByXp()
                .get(level)
                .getRolesToGive();

        List<Long> rolesToRemove = new ArrayList<>();
        for (Integer integer : xpController.getLevelByXp().keySet()) {
            if (integer >= level) break;

            Level tempLevel = xpController.getLevelByXp().get(integer);
            if (tempLevel.getRolesToGive().isEmpty()) continue;

            for (Long roleID : tempLevel.getRolesToGive()) {

                if (roleID.equals(722957999949348935L)
                        || roleID.equals(722116789055782912L)
                        || roleID.equals(770371418177011713L)) continue;

                rolesToRemove.add(roleID);
            }

        }

        if (rolesToGive != null && !rolesToGive.isEmpty()) {

            for (long roleID : rolesToGive) {

                Role role = Startup.getLauren().getGuild().getRoleById(roleID);
                if (role == null) {

                    logger.warning("Role is null");
                    continue;

                }

                Startup.getLauren().getGuild().addRoleToMember(userID, role).queue();

            }

            for (long roleID : rolesToRemove) {

                Role role = Startup.getLauren().getGuild().getRoleById(roleID);
                if (role == null) {

                    logger.warning("Role is null");
                    continue;

                }

                Startup.getLauren().getGuild().removeRoleFromMember(userID, role).queue();

            }
        }

        String message = "Parabéns <@" + userID + "> você alcançou o nível **__" + level + "__** <a:tutut:770408915300384798>";

        if (level == 20) {

            message = "<:prime:722115525232296056> O jogador <@" + userID + "> tornou-se Prime";
            addPermission("role.prime");

        }

        TextChannel channel = Startup.getLauren().getGuild().getTextChannelById(770393139516932158L);
        if (channel != null) {

            channel.sendMessage(message).queue();
            if (level == 30)
                channel.sendMessage("<:oi:762303876732420176> O jogador <@" + userID + "> tornou-se DJ").queue();

        }

        UserUtil.INSTANCE.updateNickByLevel(this, level);
        statsController.getStats("Evoluir Nível").suplyStats(1);

    }


    public Player updateRank() {

        this.rank = Rank.getByPoints(rankedPoints);
        return this;

    }

    public Player addMoney(double quantity) {
        double multiplier = multiply();

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

        double multiplier = multiply();

        quantity *= multiplier;
        experience += quantity;

        int nextLevel = level + 1;
        if (xpController.canUpgrade(nextLevel, experience)) updateLevel(nextLevel);

        statsController.getStats("Ganhar XP").suplyStats(1);

        return this;
    }

    public void executeVote() {

        this.abbleToDaily = true;
        this.voteDelay = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(12);
        this.gainXP(250);
        this.addMoney(75);
        this.setRankedPoints(this.getRankedPoints() + 2);
        ++this.votes;

    }

    @Override
    public double multiply() {

        assertList();

        double multiplier = 1;
        multiplier += getPermissions().stream().filter(multiplierList::containsKey).mapToDouble(multiplierList::get).sum();

        return multiplier;

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


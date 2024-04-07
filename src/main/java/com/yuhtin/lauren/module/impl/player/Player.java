package com.yuhtin.lauren.module.impl.player;

import com.yuhtin.lauren.Lauren;
import com.yuhtin.lauren.Startup;
import com.yuhtin.lauren.core.punish.PunishmentType;
import com.yuhtin.lauren.module.impl.level.Level;
import com.yuhtin.lauren.database.MongoOperation;
import com.yuhtin.lauren.database.OperationFilter;
import com.yuhtin.lauren.module.Module;
import com.yuhtin.lauren.module.impl.level.LevelModule;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.val;
import net.dv8tion.jda.api.entities.Guild;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@Data
@RequiredArgsConstructor
public class Player {

    private final long id;

    private long leaveTime;
    private int money = 100;

    private int lootBoxes = 0;

    private long voteDelay;
    private int votes = 0;

    private int keys = 0;
    private boolean abbleToDaily = true;

    private int level = 0;
    private int experience = 0;

    private int rankedPoints = 0;
    private Rank rank = Rank.NONE;

    private final HashMap<PunishmentType, Long> punishs = new HashMap<>();
    private final List<String> permissions = new ArrayList<>();

    public void updateLevel(int currentLevel) {
        this.level = currentLevel;

        LevelModule levelModule = Module.instance(LevelModule.class);
        if (levelModule == null) return;

        List<Long> rolesToGive = levelModule.getLevel(currentLevel).getRolesToGive();

        Lauren lauren = Startup.getLauren();
        Guild guild = lauren.getGuild();
        Logger logger = lauren.getLogger();

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

        var message = "Parabéns <@" + id + "> você alcançou o nível **__" + currentLevel + "__** <a:tutut:770408915300384798>";

        if (currentLevel == 20) {
            message += "\n<:prime:722115525232296056> O jogador <@" + id + "> tornou-se Prime";
            permissions.add("role.prime");
        }

        //TODO: statsController.getStats("Evoluir Nível").suplyStats(1);
    }

    public void updateRank() {
        this.rank = Rank.getByPoints(rankedPoints);
    }

    public void addMoney(double quantity) {
        val multiplier = calculateMultipliers();

        quantity *= multiplier;
        money += (int) Math.floor(quantity);
    }

    public void removeMoney(int quantity) {
        money -= quantity;
    }

    public void gainXP(double quantity) {
        double multiplier = calculateMultipliers();

        quantity *= multiplier;
        experience += (int) Math.floor(quantity);

        int nextLevel = level + 1;

        // TODO
//        if (xpController.canUpgrade(nextLevel, experience)) updateLevel(nextLevel);
//
//        statsController.getStats("Ganhar XP").suplyStats(1);
    }

    public double calculateMultipliers() {
        // TODO: Do a MultiplierModule
//        return 1 + permissions.stream()
//                .filter(multiplierList::containsKey)
//                .mapToDouble(multiplierList::get)
//                .sum();

        return 1;
    }

    public void executeVote() {
        abbleToDaily = true;
        voteDelay = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(12);

        gainXP(250);
        addMoney(75);
        setRankedPoints(this.getRankedPoints() + 2);
        ++votes;

    }

    public void save() {
        MongoOperation.bind(Player.class)
                .filter(OperationFilter.EQUALS, "id", id)
                .insert(this)
                .queue();
    }
}

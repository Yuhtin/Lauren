package com.yuhtin.lauren.tasks;

import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.core.player.Player;
import com.yuhtin.lauren.core.player.controller.PlayerController;
import com.yuhtin.lauren.core.punish.PunishmentType;
import com.yuhtin.lauren.core.statistics.StatsController;
import com.yuhtin.lauren.sql.provider.document.Document;
import com.yuhtin.lauren.utils.serialization.player.PlayerSerializer;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.sharding.ShardManager;

import java.util.HashMap;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

@AllArgsConstructor
public class AutoSaveTask extends TimerTask {

    private final StatsController statsController;
    private final PlayerController playerController;
    private final ShardManager shardManager;
    private final Logger logger;

    @Override
    public void run() {

        this.statsController.getStats().values().forEach(statsController.getStatisticDAO()::updateStatistic);
        this.playerController.savePlayers();

        String sql = "SELECT * FROM `lauren_players`";

        Guild guild = shardManager.getShards().get(0).getGuilds().get(0);
        Role muteRole = guild.getRoleById(760242509355024404L);
        Role callRole = guild.getRoleById(771203970118975501L);

        if (muteRole == null || callRole == null) {
            this.logger.info("Can't execute PunishmentCheckerTask (roles cannot be null)");
            return;
        }

        for (Document document : this.playerController.getPlayerDAO().queryMany(sql)) {

            Player data = PlayerSerializer.deserialize(document.getString("data"));

            // purge player's data after 7 days
            if (data.getLeaveTime() != 0 && data.getLeaveTime() + TimeUnit.DAYS.toMillis(7) < System.currentTimeMillis()) {

                this.playerController.getPlayerDAO().deletePlayer(data.getUserID());
                continue;

            }

            if (data.getPunishs() == null) data.setPunishs(new HashMap<>());
            for (PunishmentType punishmentType : data.getPunishs().keySet()) {

                // compare the punishment time and see if the player can be unpunished
                if (data.getPunishs().get(punishmentType) < System.currentTimeMillis()) {

                    data.getPunishs().remove(punishmentType);

                    Role role = punishmentType == PunishmentType.MUTE ? muteRole : callRole;
                    guild.removeRoleFromMember(data.getUserID(), role).queue();

                }
            }

        }

    }
}

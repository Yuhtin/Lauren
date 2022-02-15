package com.yuhtin.lauren.tasks;

import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.core.player.Player;
import com.yuhtin.lauren.core.player.controller.PlayerController;
import com.yuhtin.lauren.core.player.serializer.PlayerSerializer;
import com.yuhtin.lauren.core.punish.PunishmentType;
import com.yuhtin.lauren.core.statistics.StatsController;
import com.yuhtin.lauren.sql.provider.document.Document;
import lombok.AllArgsConstructor;
import lombok.val;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Role;

import java.util.HashMap;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

@AllArgsConstructor
public class AutoSaveTask extends TimerTask {

    private final StatsController statsController;
    private final PlayerController playerController;
    private final JDA bot;
    private final Logger logger;

    @Override
    public void run() {

        statsController.getStats().values().forEach(statsController.getStatisticDAO()::updateStatistic);
        playerController.savePlayers();

        val sql = "SELECT * FROM `lauren_players`";

        val guild = bot.getGuilds().get(0);
        val muteRole = guild.getRoleById(760242509355024404L);
        val callRole = guild.getRoleById(771203970118975501L);

        if (muteRole == null || callRole == null) {
            logger.info("Can't execute PunishmentCheckerTask (roles cannot be null)");
            return;
        }

        for (val document : playerController.getPlayerDAO().queryMany(sql)) {

            val data = PlayerSerializer.deserialize(document.getString("data"));

            // purge player's data after 7 days
            if (data.getLeaveTime() != 0 && data.getLeaveTime() + TimeUnit.DAYS.toMillis(7) < System.currentTimeMillis()) {
                playerController.getPlayerDAO().deletePlayer(data.getUserID());
                continue;
            }

            if (data.getPunishs() == null) data.setPunishs(new HashMap<>());
            for (val punishmentType : data.getPunishs().keySet()) {
                // compare the punishment time and see if the player can be unpunished
                if (data.getPunishs().get(punishmentType) < System.currentTimeMillis()) {
                    data.getPunishs().remove(punishmentType);

                    val role = punishmentType == PunishmentType.MUTE ? muteRole : callRole;
                    guild.removeRoleFromMember(data.getUserID(), role).queue();
                }
            }

        }

    }
}

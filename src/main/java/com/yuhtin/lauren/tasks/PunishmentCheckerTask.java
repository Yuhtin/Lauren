package com.yuhtin.lauren.tasks;

import com.yuhtin.lauren.Lauren;
import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.core.player.Player;
import com.yuhtin.lauren.core.player.controller.PlayerDatabase;
import com.yuhtin.lauren.core.punish.PunishmentType;
import com.yuhtin.lauren.database.DatabaseController;
import com.yuhtin.lauren.utils.serialization.PlayerSerializer;
import net.dv8tion.jda.api.entities.Role;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class PunishmentCheckerTask extends TimerTask {

    @Override
    public void run() {

        String sql = "SELECT * FROM `lauren_players`";

        Role muteRole = Lauren.getInstance().getGuild().getRoleById(760242509355024404L);
        Role callRole = Lauren.getInstance().getGuild().getRoleById(771203970118975501L);

        if (muteRole == null || callRole == null) {
            Logger.log("Can't execute PunishmentCheckerTask (roles cannot be null)").save();
            return;
        }

        try (PreparedStatement statement = DatabaseController.get().getConnection().prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Player data = PlayerSerializer.deserialize(resultSet.getString("data"));

                // purge player's data after 7 days
                if (data.getLeaveTime() != 0 && data.getLeaveTime() + TimeUnit.DAYS.toMillis(7) < System.currentTimeMillis()) {
                    PlayerDatabase.purge(resultSet.getLong("id"));
                    continue;
                }

                if (data.getPunishs() == null) data.setPunishs(new HashMap<>());
                for (PunishmentType punishmentType : data.getPunishs().keySet()) {

                    // compare the punishment time and see if the player can be unpunished
                    if (data.getPunishs().get(punishmentType) < System.currentTimeMillis()) {

                        Role role = punishmentType == PunishmentType.MUTE ? muteRole : callRole;
                        Lauren.getInstance()
                                .getGuild()
                                .removeRoleFromMember(data.getUserID(), role)
                                .queue();

                    }
                }
            }

            resultSet.close();

        } catch (Exception exception) {
            Logger.log("Can't check punishment's");
            exception.printStackTrace();
        }
    }
}

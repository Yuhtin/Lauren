package com.yuhtin.lauren.utils.helper;

import com.yuhtin.lauren.Lauren;
import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.core.player.Player;
import com.yuhtin.lauren.core.player.controller.PlayerController;
import com.yuhtin.lauren.core.punish.PunishmentRule;
import com.yuhtin.lauren.core.punish.PunishmentType;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class PunishmentUtils {

    private PunishmentUtils() {
        Logger.log("Unable to instantiate a utility class");
    }

    public void applyPunish(User author, User user, PunishmentRule rule) {
        PunishmentType type = rule.getType();

        if (type == PunishmentType.BAN) {

            Logger.log(Utilities.INSTANCE.getFullName(author) + " banned user " + Utilities.INSTANCE.getFullName(user) + " in rule " + rule.toString());
            Lauren.getInstance().getGuild().ban(user, 7).queue();
            return;
        }


        Player player = PlayerController.INSTANCE.get(user.getIdLong());

        long unpunishTime = System.currentTimeMillis() + rule.getPunishTime();
        player.punishs.put(type, unpunishTime);



        sendPunishMessage(user, rule);
    }

    private static void sendPunishMessage(User user, PunishmentRule rule) {
        TextChannel announcementChannel = Lauren.getInstance().getGuild().getTextChannelById(771384145027792986L);
        EmbedBuilder announcementMessage = new EmbedBuilder();

        if (announcementChannel != null) announcementChannel.sendMessage(announcementMessage.build()).queue();

        PrivateChannel privateChannel = user.openPrivateChannel().complete();
        if (privateChannel == null) return;

        EmbedBuilder privateMessage = new EmbedBuilder();
        privateChannel.sendMessage(privateMessage.build()).queue();
    }
}

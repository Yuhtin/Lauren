package com.yuhtin.lauren.utils.helper;

import com.sedmelluq.discord.lavaplayer.filter.FloatPcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.equalizer.Equalizer;
import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat;
import com.yuhtin.lauren.Lauren;
import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.core.music.TrackManager;
import com.yuhtin.lauren.core.player.Player;
import com.yuhtin.lauren.core.player.controller.PlayerController;
import com.yuhtin.lauren.core.punish.PunishmentRule;
import com.yuhtin.lauren.core.punish.PunishmentType;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.*;

public class PunishmentUtils {

    private PunishmentUtils() {
        Logger.log("Unable to instantiate a utility class");
    }

    public static void applyPunish(User author, Member user, PunishmentRule rule, String proof) {
        PunishmentType type = rule.getType();

        if (type == PunishmentType.BAN) {

            Logger.log(Utilities.INSTANCE.getFullName(author) +
                    " banned user " +
                    Utilities.INSTANCE.getFullName(user.getUser()) +
                    " in rule " +
                    rule.toString() +
                    (proof.equalsIgnoreCase("") ? "" : " with proof " + proof));

            Lauren.getInstance().getGuild().ban(user, 7, "Banned with punish system").queue();

            sendPunishMessage(author, user.getUser(), rule, proof);
            return;
        }

        Player player = PlayerController.INSTANCE.get(user.getIdLong());

        long duration = System.currentTimeMillis() + rule.getPunishTime();
        player.getPunishs().put(type, duration);

        Role role = Lauren.getInstance()
                .getGuild()
                .getRoleById(type == PunishmentType.MUTE
                        ? 760242509355024404L
                        : 771203970118975501L);

        if (role == null) Logger.log("Error on try to punish a user");
        else {

            Lauren.getInstance().getGuild().addRoleToMember(user, role).queue();
            sendPunishMessage(author, user.getUser(), rule, proof);

        }
    }

    private static void sendPunishMessage(User author, User user, PunishmentRule rule, String proof) {
        TextChannel announcementChannel = Lauren.getInstance().getGuild().getTextChannelById(769433732221632532L);
        String ruleDescription = "Regra " + rule.toString() + ": " + rule.getMotive() + (proof.equalsIgnoreCase("") ? "" : ", " + proof);

        if (announcementChannel != null) {
            MessageBuilder announcementMessage = new MessageBuilder();
            announcementMessage.setContent("**Usuário punido:** " + Utilities.INSTANCE.getFullName(user) + "\n" +
                    "**Punido por:** <@" + author.getId() + ">\n" +
                    "**Motivo:** " + ruleDescription
            );

            announcementChannel.sendMessage(announcementMessage.build()).queue();
        }

        PrivateChannel privateChannel = user.openPrivateChannel().complete();
        if (privateChannel == null) return;

        EmbedBuilder privateMessage = new EmbedBuilder();
        privateMessage.setAuthor(Utilities.INSTANCE.getFullName(author), null, author.getAvatarUrl());

        privateMessage.addField("<:chorano:726207542413230142>" +
                        " Você foi " + rule.getType().getFormated() + " de " + Lauren.getInstance().getGuild().getName(),
                "", false);

        privateMessage.addField("<:beacon:771543538252120094> Punido por", "`" + Utilities.INSTANCE.getFullName(author) + "`", false);

        privateMessage.addField("<:time:756767328498090044>" +
                " Motivo", ruleDescription, false);

        privateMessage.addField(":alarm_clock: Duração",
                "`" + (rule.getPunishTime() == 0L ? "Eterno" : MathUtils.format(rule.getPunishTime())) + "`", false);

        privateMessage.setFooter("© ^Aincrad™ servidor de jogos", Lauren.getInstance().getGuild().getIconUrl());

        privateChannel.sendMessage(privateMessage.build()).queue();
    }
}

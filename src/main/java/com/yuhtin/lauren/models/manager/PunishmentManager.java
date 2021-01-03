package com.yuhtin.lauren.models.manager;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.yuhtin.lauren.core.player.Player;
import com.yuhtin.lauren.core.player.controller.PlayerController;
import com.yuhtin.lauren.core.punish.PunishmentRule;
import com.yuhtin.lauren.core.punish.PunishmentType;
import com.yuhtin.lauren.utils.helper.TimeUtils;
import com.yuhtin.lauren.utils.helper.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.*;

import javax.inject.Named;
import java.util.logging.Logger;

@Singleton
public class PunishmentManager {

    @Inject @Named("main") private Logger logger;
    @Inject private PlayerController playerController;

    public void applyPunish(User author, Member user, PunishmentRule rule, String proof) {
        Player player = playerController.get(user.getIdLong());
        PunishmentType type = rule.getType();

        if (type == PunishmentType.BAN) {

            this.logger.info(Utilities.INSTANCE.getFullName(author) +
                    " banned user " +
                    Utilities.INSTANCE.getFullName(user.getUser()) +
                    " in rule " +
                    rule.toString() +
                    (proof.equalsIgnoreCase("") ? "" : " with proof " + proof));

            user.getGuild().ban(user, 7, "Banned with punish system").queue();

            sendPunishMessage(author, user, rule, proof);
            return;
        }

        long duration = System.currentTimeMillis() + rule.getPunishTime();

        // accumulate punishments
        if (player.getPunishs().containsKey(type)) player.getPunishs().replace(type, player.getPunishs().get(type) + duration);
        else player.getPunishs().put(type, duration);

        Role role = user.getGuild()
                .getRoleById(type == PunishmentType.MUTE
                        ? 760242509355024404L
                        : 771203970118975501L);

        if (role == null) this.logger.warning("Error on try to punish a user");
        else {

            user.getGuild().addRoleToMember(user, role).queue();
            sendPunishMessage(author, user, rule, proof);

        }
    }

    private void sendPunishMessage(User author, Member user, PunishmentRule rule, String proof) {

        TextChannel announcementChannel = user.getGuild().getTextChannelById(771384145027792986L);
        String ruleDescription = "Regra " + rule.toString() + ": " + rule.getMotive() + (proof.equalsIgnoreCase("") ? "" : ", " + proof);

        if (announcementChannel != null) {
            MessageBuilder announcementMessage = new MessageBuilder();
            announcementMessage.setContent("**Usuário punido:** " + Utilities.INSTANCE.getFullName(user.getUser()) + "\n" +
                    "**Punido por:** <@" + author.getId() + ">\n" +
                    "**Motivo:** " + ruleDescription
            );

            announcementChannel.sendMessage(announcementMessage.build()).queue();
        }

        PrivateChannel privateChannel = user.getUser().openPrivateChannel().complete();
        if (privateChannel == null) return;

        EmbedBuilder privateMessage = new EmbedBuilder();
        privateMessage.setAuthor(Utilities.INSTANCE.getFullName(author), null, author.getAvatarUrl());

        privateMessage.addField("<:chorano:726207542413230142>" +
                        " Você foi " + rule.getType().getFormated() + " de " + user.getUser().getName(),
                "", false);

        privateMessage.addField("<:beacon:771543538252120094> Punido por", "`" + Utilities.INSTANCE.getFullName(author) + "`", false);

        privateMessage.addField("<:time:756767328498090044>" +
                " Motivo", ruleDescription, false);

        privateMessage.addField(":alarm_clock: Duração",
                "`" + (rule.getPunishTime() == 0L ? "Eterno" : TimeUtils.formatTime(rule.getPunishTime())) + "`", false);

        privateMessage.setFooter("© ^Aincrad™ servidor de jogos", user.getGuild().getIconUrl());

        privateChannel.sendMessage(privateMessage.build()).queue();
    }
}

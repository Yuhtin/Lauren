package com.yuhtin.lauren.manager;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.core.player.Player;
import com.yuhtin.lauren.core.player.controller.PlayerController;
import com.yuhtin.lauren.core.punish.PunishmentRule;
import com.yuhtin.lauren.core.punish.PunishmentType;
import com.yuhtin.lauren.utils.TimeUtils;
import com.yuhtin.lauren.utils.UserUtil;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.*;

@Singleton
public class PunishmentManager {

    @Inject private Logger logger;
    @Inject private PlayerController playerController;

    public void applyPunish(User author, Member user, PunishmentRule rule, String proof) {
        val player = playerController.get(user.getIdLong());
        val type = rule.getType();

        if (type == PunishmentType.BAN) {

            this.logger.info(String.format(
                    "%s banned user %s in rule %s %s",
                    author.getAsTag(),
                    user.getUser().getAsTag(),
                    rule,
                    (proof.equalsIgnoreCase("") ? "" : " with proof " + proof)
            ));

            user.getGuild().ban(user, 7, "Banned with punish system").queue();

            sendPunishMessage(author, user, rule, proof);
            return;
        }

        long duration = System.currentTimeMillis() + rule.getPunishTime();

        // accumulate punishments
        if (player.getPunishs().containsKey(type))
            player.getPunishs().replace(type, player.getPunishs().get(type) + duration);
        else player.getPunishs().put(type, duration);

        val role = user.getGuild()
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
        val announcementChannel = user.getGuild().getTextChannelById(771384145027792986L);
        val ruleDescription = "Regra " + rule.toString() + ": " + rule.getMotive() + (proof.equalsIgnoreCase("") ? "" : ", " + proof);

        if (announcementChannel != null) {
            val announcementMessage = new MessageBuilder();
            announcementMessage.setContent("**Usuário punido:** " + user.getUser().getAsTag() + "\n" +
                    "**Punido por:** <@" + author.getId() + ">\n" +
                    "**Motivo:** " + ruleDescription
            );

            announcementChannel.sendMessage(announcementMessage.build()).queue();
        }

        val privateChannel = user.getUser().openPrivateChannel().complete();
        if (privateChannel == null) return;

        val privateMessage = new EmbedBuilder();
        privateMessage.setAuthor(author.getAsTag(), null, author.getAvatarUrl());

        privateMessage.addField("<:chorano:726207542413230142>" +
                        " Você foi " + rule.getType().getFormated() + " de " + user.getUser().getName(),
                "", false);

        privateMessage.addField("<:beacon:771543538252120094> Punido por", "`" +  author.getAsTag() + "`", false);

        privateMessage.addField("<:time:756767328498090044>" +
                " Motivo", ruleDescription, false);

        privateMessage.addField(":alarm_clock: Duração",
                "`" + (rule.getPunishTime() == 0L ? "Eterno" : TimeUtils.formatTime(rule.getPunishTime())) + "`", false);

        privateMessage.setFooter("© ^Aincrad™ servidor de jogos", user.getGuild().getIconUrl());

        privateChannel.sendMessageEmbeds(privateMessage.build()).queue();
    }
}

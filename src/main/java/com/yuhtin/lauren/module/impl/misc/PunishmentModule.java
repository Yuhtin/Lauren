package com.yuhtin.lauren.module.impl.misc;

import com.yuhtin.lauren.Lauren;
import com.yuhtin.lauren.core.punish.PunishmentRule;
import com.yuhtin.lauren.core.punish.PunishmentType;
import com.yuhtin.lauren.module.Module;
import com.yuhtin.lauren.module.impl.player.module.PlayerModule;
import com.yuhtin.lauren.util.LoggerUtil;
import com.yuhtin.lauren.util.TimeUtils;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class PunishmentModule implements Module {

    @Override
    public boolean setup(Lauren lauren) {
        return true;
    }

    public void applyPunish(User author, Member user, PunishmentRule rule, String proof) {
        Logger logger = LoggerUtil.getLogger();

        PlayerModule playerModule = Module.instance(PlayerModule.class);

        // TODO
        val player = playerController.get(user.getIdLong());
        val type = rule.getType();

        if (type == PunishmentType.BAN) {
            logger.info(String.format(
                    "%s banned user %s in rule %s %s",
                    author.getName(),
                    user.getUser().getName(),
                    rule,
                    (proof.equalsIgnoreCase("") ? "" : " with proof " + proof)
            ));

            user.getGuild().ban(user, 7, TimeUnit.DAYS).queue();

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

        if (role == null) logger.warning("Error on try to punish a user");
        else {
            user.getGuild().addRoleToMember(user, role).queue();
            sendPunishMessage(author, user, rule, proof);
        }
    }

    private void sendPunishMessage(User author, Member user, PunishmentRule rule, String proof) {
        val announcementChannel = user.getGuild().getTextChannelById(771384145027792986L);
        val ruleDescription = "Regra " + rule.toString() + ": " + rule.getMotive() + (proof.equalsIgnoreCase("") ? "" : ", " + proof);

        if (announcementChannel != null) {
            val announcementMessage = new MessageCreateBuilder();
            announcementMessage.setContent("**Usuário punido:** " + user.getUser().getName() + "\n" +
                    "**Punido por:** <@" + author.getId() + ">\n" +
                    "**Motivo:** " + ruleDescription
            );

            announcementChannel.sendMessage(announcementMessage.build()).queue();
        }

        val privateChannel = user.getUser().openPrivateChannel().complete();
        if (privateChannel == null) return;

        val privateMessage = new EmbedBuilder();
        privateMessage.setAuthor(author.getName(), null, author.getAvatarUrl());

        privateMessage.addField("<:chorano:726207542413230142>" +
                        " Você foi " + rule.getType().getFormated() + " de " + user.getUser().getName(),
                "", false);

        privateMessage.addField("<:beacon:771543538252120094> Punido por", "`" +  author.getName() + "`", false);

        privateMessage.addField("<:time:756767328498090044>" +
                " Motivo", ruleDescription, false);

        privateMessage.addField(":alarm_clock: Duração",
                "`" + (rule.getPunishTime() == 0L ? "Eterno" : TimeUtils.formatTime(rule.getPunishTime())) + "`", false);

        privateMessage.setFooter("© ^Aincrad™ servidor de jogos", user.getGuild().getIconUrl());

        privateChannel.sendMessageEmbeds(privateMessage.build()).queue();
    }
}

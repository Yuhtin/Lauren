package com.yuhtin.lauren.events;

import com.yuhtin.lauren.application.Lauren;
import com.yuhtin.lauren.core.draw.controller.DrawController;
import com.yuhtin.lauren.core.match.Match;
import com.yuhtin.lauren.core.match.controller.MatchController;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.concurrent.TimeUnit;

public class MemberReactionEvent extends ListenerAdapter {

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        if (event.getUser() == null || event.getUser().isBot() || event.getMember() == null) return;

        if (event.getMessageIdLong() == Lauren.config.resgistrationId) {
            Role boy = Lauren.guild.getRolesByName("PLEBEU\uD83D\uDC68", true).get(0), girl = Lauren.bot.getRolesByName("PLEBÉIA \uD83D\uDC69", true).get(0);
            if (boy == null || girl == null) return;

            event.getGuild().addRoleToMember(event.getMember(), event.getReactionEmote().getId().equals("704293077623504957") ? girl : boy).queue();
            event.getChannel().sendMessage("<a:sim:704295025374265387> " + event.getMember().getEffectiveName() + " o seu cargo foi adicionado!").queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS));
            event.getReaction().removeReaction(event.getUser()).queue();
            return;
        }

        if (DrawController.get() != null && DrawController.get().message.getIdLong() == event.getMessageIdLong() && !DrawController.get().finished) {
            DrawController.get().users.add(event.getUserIdLong());
            return;
        }

        if (event.getChannel().getName().startsWith("match-")) {
            Match match = MatchController.matches.get(event.getChannel().getName().split("-")[1]);
            if (match.startTime != 0) return;

            match.confirmedPlayers.add(event.getUserIdLong());
            event.getChannel().sendMessage("Você foi confirmado na lita de partida").queue();
        }
    }
}

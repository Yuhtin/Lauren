package com.yuhtin.lauren.events;

import com.yuhtin.lauren.LaurenStartup;
import com.yuhtin.lauren.core.draw.controller.DrawController;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.concurrent.TimeUnit;

public class MemberReactionEvent extends ListenerAdapter {

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        if (event.getUser() == null || event.getUser().isBot() || event.getMember() == null) return;

        if (event.getMessageIdLong() == LaurenStartup.getInstance().getConfig().getResgistrationId()) {
            Role boy = LaurenStartup.getInstance()
                    .getGuild()
                    .getRolesByName("PLEBEU\uD83D\uDC68", true).get(0);

            Role girl = LaurenStartup.getInstance()
                    .getBot()
                    .getRolesByName("PLEBÉIA \uD83D\uDC69", true).get(0);

            if (boy == null || girl == null) return;

            Role roleToGive = event.getReactionEmote().getId().equals("704293077623504957") ? girl : boy;
            event.getGuild()
                    .addRoleToMember(event.getMember(), roleToGive)
                    .queue();

            event.getChannel()
                    .sendMessage("<a:sim:704295025374265387> " + event.getMember().getEffectiveName() + " o seu cargo foi adicionado!")
                    .queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS));

            event.getReaction().removeReaction(event.getUser()).queue();
            return;
        }

        if (DrawController.get() != null
                && DrawController.get().message.getIdLong() == event.getMessageIdLong()
                && !DrawController.get().finished) {

            DrawController.get().users.add(event.getUserIdLong());
        }
    }
}

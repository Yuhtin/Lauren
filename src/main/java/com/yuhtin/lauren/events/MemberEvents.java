package com.yuhtin.lauren.events;

import com.yuhtin.lauren.application.Lauren;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

public class MemberEvents extends ListenerAdapter {

    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        boolean newAccount = ChronoUnit.DAYS.between(OffsetDateTime.now(), event.getUser().getTimeCreated()) < 3;
        boolean isDefaultAvatar = event.getUser().getAvatarUrl() == null || event.getUser().getAvatarUrl().startsWith("https://discordapp.com/");
        boolean domaincount = event.getUser().getName().matches("https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)");

        if (domaincount && (isDefaultAvatar ||newAccount)) {
            event.getUser().openPrivateChannel().queue(channel -> {
                channel.sendMessage("Olá! você foi kickado automaticamente por suspeita de divulgação em nosso servidor.\nContas com menos de 3 dias no discord não podem ter domínios (exemplo twitter.com)").queue();
                event.getMember().kick("Autokick: Selfbots não são bem vindos").queue();
            });
        }

        Lauren.data.create(event.getUser().getIdLong());
    }
}

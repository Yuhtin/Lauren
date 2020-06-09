package events;

import logger.Logger;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.time.Instant;
import java.util.Random;

public class MemberEvents extends ListenerAdapter {

    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        Logger.log("Player " + event.getUser().getName() + " joined on server").save();
    }
}

package events;

import core.logger.Logger;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MemberEvents extends ListenerAdapter {

    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        Logger.log("Player " + event.getUser().getName() + " joined on server").save();
    }
}

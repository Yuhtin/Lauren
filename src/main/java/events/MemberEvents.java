package events;

import application.Lauren;
import models.cache.PlayerDataCache;
import models.data.PlayerData;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MemberEvents extends ListenerAdapter {

    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        PlayerDataCache.insert(new PlayerData(event.getUser().getIdLong()));
        Lauren.data.create(event.getUser().getIdLong());
    }
}

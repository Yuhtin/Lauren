package com.yuhtin.lauren.events;

import com.yuhtin.lauren.application.Lauren;
import com.yuhtin.lauren.models.cache.PlayerDataCache;
import com.yuhtin.lauren.models.data.PlayerData;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MemberEvents extends ListenerAdapter {

    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        PlayerDataCache.insert(new PlayerData(event.getUser().getIdLong()));
        Lauren.data.create(event.getUser().getIdLong());
    }
}

package com.yuhtin.lauren.events;

import com.yuhtin.lauren.application.Lauren;
import com.yuhtin.lauren.manager.PlayerDataManager;
import com.yuhtin.lauren.models.data.PlayerData;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MemberEvents extends ListenerAdapter {

    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        Lauren.data.create(event.getUser().getIdLong());
    }
}

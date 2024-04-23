package com.yuhtin.lauren.events;

import com.yuhtin.lauren.module.Module;
import com.yuhtin.lauren.module.impl.player.module.PlayerModule;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class MemberJoinEvent extends ListenerAdapter {

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        PlayerModule playerModule = Module.instance(PlayerModule.class);
        playerModule.retrieve(event.getMember().getIdLong()).thenAccept(player -> {
            player.setLeaveTime(0);
        });
    }
}

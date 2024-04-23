package com.yuhtin.lauren.events;

import com.yuhtin.lauren.module.Module;
import com.yuhtin.lauren.module.impl.player.module.PlayerModule;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class MemberQuitEvent extends ListenerAdapter {

    @Override
    public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event) {
        PlayerModule playerModule = Module.instance(PlayerModule.class);
        playerModule.retrieve(event.getMember().getIdLong()).thenAccept(player -> {
            player.setLeaveTime(System.currentTimeMillis());
        });
    }

}

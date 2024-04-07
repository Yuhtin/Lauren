package com.yuhtin.lauren.events;

import com.google.inject.Inject;
import com.yuhtin.lauren.core.player.controller.PlayerController;
import lombok.val;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class MemberJoinEvent extends ListenerAdapter {

    @Inject private PlayerController playerController;

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        val player = playerController.get(event.getUser().getIdLong());
        player.setLeaveTime(0);
    }
}

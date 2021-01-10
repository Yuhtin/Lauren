package com.yuhtin.lauren.events;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.yuhtin.lauren.core.player.controller.PlayerController;
import com.yuhtin.lauren.utils.helper.Utilities;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public class MemberQuitEvent extends ListenerAdapter {

    @Inject @Named("main") private Logger logger;
    @Inject private PlayerController playerController;

    @Override
    public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event) {

        this.logger.info("Setted leavetime for user " + Utilities.INSTANCE.getFullName(event.getUser()));
        this.playerController.get(event.getUser().getIdLong()).setLeaveTime(System.currentTimeMillis());

    }

}

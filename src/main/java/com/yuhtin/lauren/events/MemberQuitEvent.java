package com.yuhtin.lauren.events;

import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.core.player.controller.PlayerController;
import com.yuhtin.lauren.utils.helper.Utilities;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class MemberQuitEvent extends ListenerAdapter {

    @Override
    public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event) {

        Logger.log("Setted leavetime for user " + Utilities.INSTANCE.getFullName(event.getUser())).save();
        PlayerController.INSTANCE.get(event.getUser().getIdLong()).setLeaveTime(System.currentTimeMillis());

    }

}

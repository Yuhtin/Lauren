package com.yuhtin.lauren.events;

import com.google.inject.Inject;
import com.yuhtin.lauren.core.player.controller.PlayerController;
import com.yuhtin.lauren.startup.Startup;
import lombok.val;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateBoostTimeEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class MemberBoostEvent extends ListenerAdapter {

    @Inject private PlayerController playerController;

    @Override
    public void onGuildMemberUpdateBoostTime(@NotNull GuildMemberUpdateBoostTimeEvent event) {
        val player = playerController.get(event.getMember().getIdLong());
        val role = event.getMember()
                .getRoles()
                .stream()
                .filter(memberRole -> memberRole.getIdLong() == 750365511430307931L)
                .findAny()
                .orElse(null);

        if (role == null) player.removePermission("role.booster");
        else {
            val primeRole = Startup.getLauren()
                    .getGuild()
                    .getRoleById(722116789055782912L);

            if (primeRole != null) Startup.getLauren()
                    .getGuild()
                    .addRoleToMember(event.getMember(), primeRole)
                    .queue();

            player.addPermission("role.booster");
            player.addPermission("role.prime");
        }
    }
}

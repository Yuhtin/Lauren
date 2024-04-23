package com.yuhtin.lauren.events;

import com.yuhtin.lauren.Startup;
import com.yuhtin.lauren.module.Module;
import com.yuhtin.lauren.module.impl.player.module.PlayerModule;
import lombok.val;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateBoostTimeEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class MemberBoostEvent extends ListenerAdapter {

    @Override
    public void onGuildMemberUpdateBoostTime(@NotNull GuildMemberUpdateBoostTimeEvent event) {

        PlayerModule playerModule = Module.instance(PlayerModule.class);
        playerModule.retrieve(event.getMember().getIdLong()).thenAccept(player -> {
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
        });

    }
}

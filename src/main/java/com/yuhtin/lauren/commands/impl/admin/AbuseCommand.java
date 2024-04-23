package com.yuhtin.lauren.commands.impl.admin;

import com.yuhtin.lauren.commands.Command;
import com.yuhtin.lauren.commands.CommandInfo;
import com.yuhtin.lauren.commands.CommandType;
import com.yuhtin.lauren.module.Module;
import com.yuhtin.lauren.module.impl.player.module.PlayerModule;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;

@CommandInfo(
        name = "testsystems",
        type = CommandType.ADMIN,
        description = "Abusadamente",
        permissions = { Permission.ADMINISTRATOR }
)
public class AbuseCommand implements Command {

    @Override
    public void execute(CommandInteraction event, InteractionHook hook) throws Exception {
        if (event.getMember() == null) return;

        PlayerModule playerModule = Module.instance(PlayerModule.class);
        if (playerModule == null) return;

        playerModule.retrieve(event.getUser().getIdLong()).thenAccept(player -> {
            player.setLootBoxes(player.getLootBoxes() + 1);
            player.setMoney(10000);
            player.setKeys(0);

            hook.setEphemeral(true).sendMessage("👀 Abusei algumas coisas pra você.").queue();
        });
    }

}

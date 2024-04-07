package com.yuhtin.lauren.commands.impl.admin;

import com.google.inject.Inject;
import com.yuhtin.lauren.commands.Command;
import com.yuhtin.lauren.commands.CommandInfo;
import com.yuhtin.lauren.core.player.controller.PlayerController;
import lombok.val;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;

@CommandInfo(
        name = "testsystems",
        type = CommandType.ADMIN,
        description = "Abusadamente"
)
public class AbuseCommand implements Command {

    @Inject private PlayerController playerController;

    @Override
    public void execute(CommandInteraction event, InteractionHook hook) throws Exception {
        if (event.getMember() == null
                || !UserUtil.hasPermission(event.getMember(), hook, Permission.ADMINISTRATOR)) return;

        val player = playerController.get(event.getUser().getIdLong());
        player.setLootBoxes(player.getLootBoxes() + 1);
        player.setMoney(10000);
        player.setKeys(0);

        hook.setEphemeral(true).sendMessage("👀 Abusei algumas coisas pra você.").queue();
    }

}

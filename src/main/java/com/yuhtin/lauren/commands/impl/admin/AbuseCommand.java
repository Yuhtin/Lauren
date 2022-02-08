package com.yuhtin.lauren.commands.impl.admin;

import com.google.inject.Inject;
import com.yuhtin.lauren.core.player.controller.PlayerController;
import com.yuhtin.lauren.commands.CommandHandler;
import com.yuhtin.lauren.utils.helper.UserUtil;
import lombok.val;
import net.dv8tion.jda.api.Permission;

@CommandHandler(
        name = "machadodemadeira",
        type = CommandHandler.CommandType.ADMIN,
        description = "Abusadamente",
        alias = {}
)
public class AbuseCommand implements CommandExecutor {

    @Inject private PlayerController playerController;

    @Override
    public void execute(CommandEvent event) {
        if (!UserUtil.hasPermission(event.getMember(), event.getMessage(), Permission.ADMINISTRATOR, true))
            return;

        val player = playerController.get(event.getAuthor().getIdLong());
        player.setLootBoxes(player.getLootBoxes() + 1);
        player.setMoney(10000);
        player.setKeys(0);

        event.getMessage().addReaction(":xp:772285036174639124").queue();
    }
}

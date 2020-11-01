package com.yuhtin.lauren.commands.admin;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.yuhtin.lauren.core.player.Player;
import com.yuhtin.lauren.core.player.controller.PlayerController;
import com.yuhtin.lauren.models.annotations.CommandHandler;
import com.yuhtin.lauren.utils.helper.Utilities;
import net.dv8tion.jda.api.Permission;

@CommandHandler(
        name = "machadodemadeira",
        type = CommandHandler.CommandType.ADMIN,
        description = "Abusadamente",
        alias = {}
)
public class AbuseCommand extends Command {

    @Override
    protected void execute(CommandEvent event) {
        if (!Utilities.INSTANCE.isPermission(event.getMember(), event.getChannel(), Permission.ADMINISTRATOR, true))
            return;

        Player player = PlayerController.INSTANCE.get(event.getAuthor().getIdLong());
        ++player.keys;
        ++player.lootBoxes;
    }
}

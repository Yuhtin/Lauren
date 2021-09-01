package com.yuhtin.lauren.commands.impl.utility;

import com.google.inject.Inject;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.yuhtin.lauren.core.player.Player;
import com.yuhtin.lauren.core.player.controller.PlayerController;
import com.yuhtin.lauren.commands.CommandHandler;
import com.yuhtin.lauren.utils.helper.UserUtil;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
@CommandHandler(
        name = "showlevel",
        type = CommandHandler.CommandType.UTILITY,
        description = "Mostrar o nível em seu nickname",
        alias = {"mostrarlevel", "mostrarnivel", "shownivel"}
)
public class ShowLevelCommand extends Command {

    @Inject private PlayerController playerController;

    @Override
    protected void execute(CommandEvent event) {

        Player player = this.playerController.get(event.getAuthor().getIdLong());
        player.setHideLevelOnNickname(false);

        UserUtil.INSTANCE.updateNickByLevel(player, player.getLevel());

        event.getChannel().sendMessage(
                "<:felizpakas:742373250037710918> Você habilitou o nível em seu nickname"
        ).queue();

    }
}

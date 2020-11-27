package com.yuhtin.lauren.commands.utility;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.core.player.Player;
import com.yuhtin.lauren.core.player.controller.PlayerController;
import com.yuhtin.lauren.models.annotations.CommandHandler;
import com.yuhtin.lauren.models.enums.LogType;
import net.dv8tion.jda.api.exceptions.HierarchyException;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@CommandHandler(
        name = "desabilitarlevel",
        type = CommandHandler.CommandType.UTILITY,
        description = "Não mostrar o nível em seu nickname",
        alias = {"turnofflevel", "esconderlevel", "ocultlevel"}
)
public class TurnOffLevelCommand extends Command {

    @Override
    protected void execute(CommandEvent event) {

        Player player = PlayerController.INSTANCE.get(event.getAuthor().getIdLong());
        player.setHideLevelOnNickname(true);

        if (event.getMember().getNickname() != null) {

            String nickname = event.getMember().getNickname();
            if (nickname.contains("[")) {

                nickname = nickname.split("] ")[1];

                try {
                    event.getMember().modifyNickname(nickname).queue();
                } catch (HierarchyException ignored) {
                    Logger.log("Can't update member with role higher my self", LogType.ERROR);
                }

            }

        }

        event.getChannel().sendMessage(
                "<:felizpakas:742373250037710918> Você ocultou o nível em seu nickname"
        ).queue();

    }
}

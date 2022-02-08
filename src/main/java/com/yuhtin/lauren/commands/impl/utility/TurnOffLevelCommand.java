package com.yuhtin.lauren.commands.impl.utility;

import com.google.inject.Inject;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.yuhtin.lauren.commands.Command;
import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.core.player.Player;
import com.yuhtin.lauren.core.player.controller.PlayerController;
import com.yuhtin.lauren.commands.CommandData;
import net.dv8tion.jda.api.exceptions.HierarchyException;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@CommandData(
        name = "desabilitarlevel",
        type = CommandData.CommandType.UTILITY,
        description = "Não mostrar o nível em seu nickname",
        alias = {"turnofflevel", "esconderlevel", "ocultlevel", "ocultarnivel", "escondernivel"}
)
public class TurnOffLevelCommand implements Command {

    @Inject private Logger logger;
    @Inject private PlayerController playerController;

    @Override
    protected void execute(CommandEvent event) {

        Player player = this.playerController.get(event.getAuthor().getIdLong());
        player.setHideLevelOnNickname(true);

        if (event.getMember().getNickname() != null) {

            String nickname = event.getMember().getNickname();
            if (nickname.contains("[")) {

                nickname = nickname.split("] ")[1];

                try {
                    event.getMember().modifyNickname(nickname).queue();
                } catch (HierarchyException ignored) {
                    this.logger.warning("Can't update member with role higher my self");
                }

            }

        }

        event.getChannel().sendMessage(
                "<:felizpakas:742373250037710918> Você ocultou o nível em seu nickname"
        ).queue();

    }
}

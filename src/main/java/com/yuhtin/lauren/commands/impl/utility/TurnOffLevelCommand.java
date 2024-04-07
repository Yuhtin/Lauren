package com.yuhtin.lauren.commands.impl.utility;

import com.google.inject.Inject;
import com.yuhtin.lauren.commands.Command;
import com.yuhtin.lauren.commands.CommandInfo;
import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.core.player.controller.PlayerController;
import lombok.val;
import lombok.var;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@CommandInfo(
        name = "level.hide",
        type = CommandType.UTILITY,
        description = "Não mostrar o nível em seu nickname"
)
public class TurnOffLevelCommand implements Command {

    @Inject private Logger logger;
    @Inject private PlayerController playerController;

    @Override
    public void execute(CommandInteraction event, InteractionHook hook) throws Exception {
        val player = playerController.get(event.getUser().getIdLong());
        player.setHideLevelOnNickname(true);

        if (event.getMember().getNickname() != null) {
            var nickname = event.getMember().getNickname();
            if (nickname.contains("[")) {
                nickname = nickname.split("] ")[1];

                try {
                    event.getMember().modifyNickname(nickname).queue();
                } catch (HierarchyException ignored) {
                    logger.warning("Can't update member with role higher my self");
                }

            }

        }

        hook.sendMessage("<:felizpakas:742373250037710918> Você ocultou o nível em seu nickname").queue();

    }

}

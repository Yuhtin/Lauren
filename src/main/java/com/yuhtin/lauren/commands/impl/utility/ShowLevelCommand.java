package com.yuhtin.lauren.commands.impl.utility;

import com.google.inject.Inject;
import com.yuhtin.lauren.commands.Command;
import com.yuhtin.lauren.commands.CommandInfo;
import com.yuhtin.lauren.core.player.controller.PlayerController;
import lombok.val;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
@CommandInfo(
        name = "level.show",
        type = CommandInfo.CommandType.UTILITY,
        description = "Mostrar o nível em seu nickname"
)
public class ShowLevelCommand implements Command {

    @Inject private PlayerController playerController;

    @Override
    public void execute(CommandInteraction event, InteractionHook hook) throws Exception {
        val player = this.playerController.get(event.getUser().getIdLong());
        player.setHideLevelOnNickname(false);

        UserUtil.updateNickByLevel(player, player.getLevel());

        hook.sendMessage("<:felizpakas:742373250037710918> Você habilitou o nível em seu nickname").queue();
    }

}

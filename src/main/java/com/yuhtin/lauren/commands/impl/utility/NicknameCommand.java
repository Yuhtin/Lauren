package com.yuhtin.lauren.commands.impl.utility;

import com.yuhtin.lauren.commands.Command;
import com.yuhtin.lauren.commands.CommandInfo;
import com.yuhtin.lauren.commands.CommandType;
import com.yuhtin.lauren.module.Module;
import com.yuhtin.lauren.module.impl.player.module.PlayerModule;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;

@CommandInfo(
        name = "nickname",
        type = CommandType.UTILITY,
        description = "Comando para alterar seu apelido",
        args = {
                "<nick>-Nick que deseja usar"
        }
)
public class NicknameCommand implements Command {

    @Override
    public void execute(CommandInteraction event, InteractionHook hook) throws Exception {
        PlayerModule playerModule = Module.instance(PlayerModule.class);
        if (playerModule == null) {
            hook.sendMessage("<:chorano:726207542413230142> Ocorreu um erro ao executar o comando, tente novamente mais tarde.").queue();
            return;
        }

        playerModule.retrieve(event.getUser().getIdLong()).thenAccept(player -> {
            if (!player.getPermissions().contains("commands.nickname")) {
                hook.sendMessage("<:oi:762303876732420176> " +
                        "Você não tem permissão para usar este comando, compre-a em `/shop`.").queue();
                return;
            }

            var nick = event.getOption("nick").getAsString().replace("[", "").replace("]", "");

            if (nick.length() > 32) {
                hook.sendMessage("<:chorano:726207542413230142> O nick escolhido é muito grande").queue();
                return;
            }

            hook.sendMessage("<:feliz_pra_caralho:760202116504485948> Você modificou seu nick com sucesso (os parênteses fazem parte do sistema de nível).").queue();
            event.getMember().modifyNickname(nick).queue();
        });
    }
}

package com.yuhtin.lauren.commands.impl.music;

import com.yuhtin.lauren.commands.Command;
import com.yuhtin.lauren.commands.CommandInfo;
import com.yuhtin.lauren.core.music.TrackManager;
import lombok.val;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;

@CommandInfo(
        name = "volume",
        type = CommandInfo.CommandType.MUSIC,
        description = "Definir um volume para meus batidões (não coloca menos de 30 se não deixa de ser batidão ;-;)",
        args = {
                "[!volume]-Volume que deseja definir para a música"
        }
)
public class VolumeCommand implements Command {

    @Override
    public void execute(CommandInteraction event, InteractionHook hook) throws Exception {
        if (event.getMember() == null || event.getGuild() == null) return;

        val trackManager = TrackManager.of(event.getGuild());
        if (!UserUtil.isDJ(event.getMember(), hook)) {
            hook.sendMessage("\uD83D\uDD0A Meu volume atual está em: `" + trackManager.getPlayer().getVolume() + "%`").queue();
            return;
        }

        val option = event.getOption("volume");
        if (option == null) {
            hook.sendMessage("\uD83D\uDD0A Meu volume atual está em: `" + trackManager.getPlayer().getVolume() + "%`").queue();
            hook.sendMessage("\uD83D\uDCA2 Eita calma ai, se quiser mudar o volume, insira um valor de `1 a 100` (Padrão: 25)").queue();
            return;
        }

        var volume = (int) option.getAsDouble();
        if (volume < 1 || (volume > 100 && !UserUtil.isOwner(event.getUser(), null))) volume = 25;

        trackManager.getPlayer().setVolume(volume);
        hook.sendMessage("♻️ Opaaaa, você setou o volume dos meus batidões para `" + volume + "%`").queue();
    }

}

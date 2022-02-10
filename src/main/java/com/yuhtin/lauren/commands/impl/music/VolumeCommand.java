package com.yuhtin.lauren.commands.impl.music;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.yuhtin.lauren.commands.Command;
import com.yuhtin.lauren.core.music.TrackManager;
import com.yuhtin.lauren.commands.CommandData;
import com.yuhtin.lauren.utils.UserUtil;

@CommandData(
        name = "volume",
        type = CommandData.CommandType.MUSIC,
        description = "Definir um volume para meus batidões (não coloca menos de 30 se não deixa de ser batidão ;-;)",
        alias = {}
)
public class VolumeCommand implements Command {

    @Override
    protected void execute(CommandEvent event) {

        TrackManager trackManager = TrackManager.of(event.getGuild());
        if (!UserUtil.INSTANCE.isDJ(event.getMember(), event.getChannel(), false)) {
            event.getChannel().sendMessage("\uD83D\uDD0A Meu volume atual está em: `" + trackManager.getPlayer().getVolume() + "%`").queue();
            return;
        }

        if (event.getArgs().isEmpty()) {
            event.getChannel().sendMessage("\uD83D\uDD0A Meu volume atual está em: `" + trackManager.getPlayer().getVolume() + "%`").queue();
            event.getChannel().sendMessage("\uD83D\uDCA2 Eita calma ai, se quiser mudar o volume, insira um valor de `1 a 100` (Padrão: 25)").queue();
            return;
        }

        int volume;
        try {
            volume = Integer.parseInt(event.getArgs());
            if (volume < 1 || (volume > 100 && !UserUtil.INSTANCE.isOwner(null, event.getAuthor(), false))) volume = 25;
        } catch (Exception exception) {
            event.getChannel().sendMessage("\uD83D\uDCA2 Eita calma ai, insira um valor de `1 a 100` para ser o volume").queue();
            return;
        }

        trackManager.getPlayer().setVolume(volume);
        event.getChannel().sendMessage("♻️ Opaaaa, você setou o volume dos meus batidões para `" + volume + "%`").queue();
    }
}

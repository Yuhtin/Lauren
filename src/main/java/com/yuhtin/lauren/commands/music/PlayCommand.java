package com.yuhtin.lauren.commands.music;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.yuhtin.lauren.core.music.TrackManager;
import com.yuhtin.lauren.models.annotations.CommandHandler;
import com.yuhtin.lauren.utils.helper.TrackUtils;
import lombok.SneakyThrows;

@CommandHandler(
        name = "tocar",
        type = CommandHandler.CommandType.MUSIC,
        description = "Tocar algum somzinho ai",
        alias = {"play"}
)
public class PlayCommand extends Command {

    @Override
    protected void execute(CommandEvent event) {
        if (!TrackUtils.get().isInVoiceChannel(event.getMember())) {
            event.getChannel().sendMessage(
                    "\uD83C\uDFB6 Amiguinho, entre no canal `\uD83C\uDFB6┇Batidões` para poder usar comando de música"
            ).queue();
            return;
        }

        TrackManager trackManager = TrackManager.of(event.getGuild());
        if (!trackManager.getAudio().equals(event.getMember().getVoiceState().getChannel())) {

            event.getChannel().sendMessage("\uD83C\uDFB6 Você precisa estar no mesmo canal que eu para usar isto").queue();
            return;

        }

        String[] arguments = event.getArgs().split(" ");
        if (event.getArgs().equalsIgnoreCase("")) {

            event.getChannel().sendMessage("<a:nao:704295026036834375> Utilize :clock9: `$tocar <link ou nome>`").queue();
            return;

        }

        String input = String.join(" ", arguments);
        input = input.contains("http") ? input : "ytsearch: " + input;

        if (input.toLowerCase().contains("som de")
                || input.toLowerCase().contains("som do")
                || input.toLowerCase().contains("som da")) {
            event.getChannel().sendMessage("<a:nao:704295026036834375> Nem fodendo, hoje não vai rolar").queue();
            return;
        }

        trackManager.loadTrack(input, event.getMember(), event.getTextChannel(), TrackManager.SearchType.SIMPLE_SEARCH);
    }

}

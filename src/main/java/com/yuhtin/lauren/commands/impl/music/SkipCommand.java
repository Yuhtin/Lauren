package com.yuhtin.lauren.commands.impl.music;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.yuhtin.lauren.core.music.AudioInfo;
import com.yuhtin.lauren.core.music.TrackManager;
import com.yuhtin.lauren.models.annotations.CommandHandler;
import com.yuhtin.lauren.utils.helper.TrackUtils;
import com.yuhtin.lauren.utils.helper.Utilities;

@CommandHandler(
        name = "pular",
        type = CommandHandler.CommandType.MUSIC,
        description = "Iniciar uma votação para pular a música atual",
        alias = {"skip"}
)
public class SkipCommand extends Command {

    @Override
    protected void execute(CommandEvent event) {
        if (!TrackUtils.get().isInVoiceChannel(event.getMember())) {
            event.getChannel().sendMessage("\uD83C\uDFB6 Amiguinho, entre no canal `\uD83C\uDFB6┇Batidões` para poder usar comandos de música").queue();
            return;
        }

        if (TrackUtils.get().isIdle(event.getTextChannel())) return;

        TrackManager trackManager = TrackManager.of(event.getGuild());
        if (TrackUtils.get().isMusicOwner(event.getMember())) {
            trackManager.getPlayer().stopTrack();
            event.getChannel().sendMessage("\u23e9 Pulei a música pra você <3").queue();
            return;
        }

        AudioInfo info = trackManager.getTrackInfo();
        if (info.hasVoted(event.getAuthor())) {
            event.getChannel().sendMessage("\uD83D\uDC6E\uD83C\uDFFD\u200D♀️ Ei você já votou pra pular essa música ;-;").queue();
            return;
        }

        info.addSkip(event.getAuthor());
        if (info.getSkips() >= trackManager.getAudio().getMembers().size() - 2) {
            trackManager.getPlayer().stopTrack();
            event.getChannel().sendMessage("\uD83E\uDDF6 Amo quando todos concordam entre si, pulando a música").queue();
            return;
        }

        String name = event.getMember().getNickname() == null
                ? Utilities.INSTANCE.getFullName(event.getAuthor())
                : event.getMember().getNickname();

        String message = "\uD83E\uDDEC **"
                + name +
                "** votou para pular a música **("
                + info.getSkips() + "/" + (trackManager.getAudio().getMembers().size() - 2)
                + ")**";

        event.getChannel().sendMessage(message).queue();
    }
}

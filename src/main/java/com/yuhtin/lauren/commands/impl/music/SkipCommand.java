package com.yuhtin.lauren.commands.impl.music;

import com.yuhtin.lauren.commands.Command;
import com.yuhtin.lauren.commands.CommandInfo;
import com.yuhtin.lauren.module.impl.music.AudioInfo;
import com.yuhtin.lauren.util.MusicUtil;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;

@CommandInfo(
        name = "skip.music",
        type = CommandType.MUSIC,
        description = "Iniciar uma votação para pular a música atual"
)
public class SkipCommand implements Command {

    @Override
    public void execute(CommandInteraction event, InteractionHook hook) throws Exception {
        if (event.getMember() == null || event.getGuild() == null) return;

        if (!MusicUtil.isInVoiceChannel(event.getMember())) {
            hook.sendMessage("\uD83C\uDFB6 Amiguinho, entre em algum canal de voz para poder usar comandos de música.").queue();
            return;
        }

        if

        if (MusicUtil.isIdle(event.getGuild(), hook)) return;

        TrackManager trackManager = TrackManager.getByGuild(event.getGuild());
        if (MusicUtil.isMusicOwner(event.getMember())) {
            trackManager.getPlayer().stopTrack();
            hook.sendMessage("\u23e9 Pulei a música pra você <3").queue();
            return;
        }

        AudioInfo info = trackManager.getTrackInfo();
        if (info.hasVoted(event.getUser())) {
            hook.sendMessage("\uD83D\uDC6E\uD83C\uDFFD\u200D♀️ Ei você já votou pra pular essa música ;-;").queue();
            return;
        }

        info.addSkip(event.getUser());
        if (info.getSkips() >= trackManager.getAudio().getMembers().size() - 2) {
            trackManager.skipTrack();
            hook.sendMessage("\uD83E\uDDF6 Amo quando todos concordam entre si, pulando a música").queue();
            return;
        }

        String name = event.getMember().getNickname() == null
                ? event.getUser().getName()
                : event.getMember().getNickname();

        String message = "\uD83E\uDDEC **"
                + name +
                "** votou para pular a música **("
                + info.getSkips() + "/" + (trackManager.getAudio().getMembers().size() - 2)
                + ")**";

        hook.sendMessage(message).queue();
    }
}

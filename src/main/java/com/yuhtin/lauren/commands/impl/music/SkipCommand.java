package com.yuhtin.lauren.commands.impl.music;

import com.yuhtin.lauren.commands.Command;
import com.yuhtin.lauren.commands.CommandInfo;
import com.yuhtin.lauren.commands.CommandType;
import com.yuhtin.lauren.module.Module;
import com.yuhtin.lauren.module.impl.music.AudioInfo;
import com.yuhtin.lauren.module.impl.music.MusicModule;
import com.yuhtin.lauren.module.impl.player.module.PlayerModule;
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

        PlayerModule playerModule = Module.instance(PlayerModule.class);
        if (playerModule == null) return;

        MusicModule musicModule = Module.instance(MusicModule.class);
        if (musicModule == null) return;

        musicModule.getByGuildId(event.getGuild()).queue(trackManager -> {
            if (MusicUtil.isIdle(trackManager, hook)) return;

            if (MusicUtil.isMusicOwner(event.getMember(), trackManager)) {
                trackManager.getPlayer().stopTrack();
                hook.sendMessage("\u23e9 Pulei a música pra você <3").queue();
                return;
            }

            AudioInfo info = trackManager.getTrackInfo();
            if (info.hasVoted(event.getUser())) {
                hook.sendMessage("\uD83D\uDC6E\uD83C\uDFFD\u200D♀️ Ei você já votou pra pular essa música ;-;")
                        .setEphemeral(true)
                        .queue();
                return;
            }

            info.addSkip(event.getUser());
            if (info.getSkips() >= trackManager.getAudioChannel().getMembers().size() - 2) {
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
                    + info.getSkips() + "/" + (trackManager.getAudioChannel().getMembers().size() - 2)
                    + ")**";

            hook.sendMessage(message).queue();
        });
    }
}

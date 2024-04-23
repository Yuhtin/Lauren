package com.yuhtin.lauren.commands.impl.music;

import com.yuhtin.lauren.commands.Command;
import com.yuhtin.lauren.commands.CommandInfo;
import com.yuhtin.lauren.commands.CommandType;
import com.yuhtin.lauren.module.Module;
import com.yuhtin.lauren.module.impl.music.MusicModule;
import com.yuhtin.lauren.module.impl.player.module.PlayerModule;
import com.yuhtin.lauren.util.MusicUtil;
import com.yuhtin.lauren.util.TimeUtils;
import lombok.val;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;

import java.util.concurrent.TimeUnit;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@CommandInfo(
        name = "skip.to",
        type = CommandType.MUSIC,
        description = "Pular para uma certa minutagem na música",
        args = {"<tempo>-Exemplo de tempos válidos: **35** ou **15:13**"}
)
public class JumpToTimeCommand implements Command {

    @Override
    public void execute(CommandInteraction event, InteractionHook hook) {
        if (event.getGuild() == null || event.getMember() == null) return;

        PlayerModule playerModule = Module.instance(PlayerModule.class);
        if (playerModule == null) return;

        MusicModule musicModule = Module.instance(MusicModule.class);
        if (musicModule == null) return;

        musicModule.getByGuildId(event.getGuild()).queue(trackManager -> {
            if (MusicUtil.isIdle(trackManager, hook)) return;

            if (!MusicUtil.isMusicOwner(event.getMember(), trackManager) && !playerModule.isDJ(event.getMember())) {
                hook.sendMessage("Você não é o dono da música, então não pode pular ela <3").setEphemeral(true).queue();
                return;
            }

            val args = event.getOption("tempo").getAsString();
            long millis;
            if (!args.contains(":")) {
                long secondsInMillis = parseInt(args, TimeUnit.SECONDS, hook);
                if (secondsInMillis == -1) return;

                millis = secondsInMillis;
            } else {
                val split = args.split(":");
                val secondsInMillis = parseInt(split[split.length - 1], TimeUnit.SECONDS, hook);
                if (secondsInMillis == -1) return;

                var minutesInMillis = 0L;
                if (split.length > 1) {
                    minutesInMillis = parseInt(split[split.length - 2], TimeUnit.MINUTES, hook);
                    if (minutesInMillis == -1) return;
                }

                var hoursInMillis = 0L;
                if (split.length > 2) {
                    hoursInMillis = parseInt(split[split.length - 3], TimeUnit.HOURS, hook);
                    if (hoursInMillis == -1) return;
                }

                millis = hoursInMillis + minutesInMillis + secondsInMillis;
            }

            val track = trackManager.getTrackInfo().getTrack();
            long duration = track.getDuration();
            if (millis > duration) {
                hook.sendMessage("<:pensando:781761324547309594> A música é menor que o tempo inserido, se quiser skipar use `/skip`").setEphemeral(true).queue();
                return;
            }

            track.setPosition(millis);
            hook.sendMessage("<:felizpakas:742373250037710918> Pulando a música para `" + TimeUtils.formatTime(millis) + "`").queue();
        });
    }

    private long parseInt(String arg, TimeUnit timeUnit, InteractionHook hook) {
        try {
            var time = Integer.parseInt(arg);

            if (time < 0) time = 0;
            else if (timeUnit == TimeUnit.SECONDS && time > 60) time = 60;
            else if (timeUnit == TimeUnit.MINUTES && time > 60) time = 60;
            else if (timeUnit == TimeUnit.HOURS && time > 24) time = 24;

            return timeUnit.toMillis(time);
        } catch (Exception exception) {
            hook.sendMessage("<:pensando:781761324547309594> Você digitou um tempo inválido").setEphemeral(true).queue();
            return -1;
        }

    }
}

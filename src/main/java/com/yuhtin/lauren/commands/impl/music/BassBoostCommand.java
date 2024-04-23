package com.yuhtin.lauren.commands.impl.music;

import com.yuhtin.lauren.commands.Command;
import com.yuhtin.lauren.commands.CommandInfo;
import com.yuhtin.lauren.commands.CommandType;
import com.yuhtin.lauren.module.Module;
import com.yuhtin.lauren.module.impl.music.EqualizerPreset;
import com.yuhtin.lauren.module.impl.music.MusicModule;
import com.yuhtin.lauren.module.impl.player.module.PlayerModule;
import lombok.val;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;

@CommandInfo(
        name = "bassboost",
        type = CommandType.MUSIC,
        description = "Mudar os graves e agudos do meu batidão",
        args = {"<boost>-Opções válidas low, high, boost ou normal"}
)
public class BassBoostCommand implements Command {

    @Override
    public void execute(CommandInteraction event, InteractionHook hook) {
        PlayerModule playerModule = Module.instance(PlayerModule.class);

        if (playerModule == null
                || event.getGuild() == null
                || event.getMember() == null) return;

        if (!playerModule.isDJ(event.getMember())) {
            hook.sendMessage(":no_entry: Você não tem permissão para usar esse comando!").setEphemeral(true).queue();
            return;
        }

        MusicModule musicModule = Module.instance(MusicModule.class);
        if (musicModule == null) return;

        musicModule.getByGuildId(event.getGuild()).queue(trackManager -> {
            if (trackManager == null) return;

            val boost = event.getOption("boost").getAsString();
            switch (boost) {

                case "low":
                    trackManager.getEqualizer().equalize(trackManager.getPlayer(), EqualizerPreset.LOW_BASS, -.35f);
                    hook.sendMessage(":loud_sound: Equalizando o baixo da música!").queue();
                    break;

                case "high":
                    trackManager.getEqualizer().equalize(trackManager.getPlayer(), EqualizerPreset.HIGH, .083f);
                    hook.sendMessage(":loud_sound: Equalizando o grave da música!").queue();
                    break;

                case "boost":
                    trackManager.getEqualizer().equalize(trackManager.getPlayer(), EqualizerPreset.BASS_BOOST, .12f);
                    hook.sendMessage(":loud_sound: Equalizando tudão, cuidado rapaziada!").queue();
                    break;


                case "normal":

                    trackManager.getEqualizer().equalize(trackManager.getPlayer(), EqualizerPreset.HIGH, 0);
                    trackManager.getEqualizer().equalize(trackManager.getPlayer(), EqualizerPreset.LOW_BASS, 0);
                    hook.sendMessage(":loud_sound: Tirando equalização").queue();
                    break;


                default:
                    hook.sendMessage(":grey_question: Não encontrei essa equalização, equalizações válidas: `low, high, boost ou normal`")
                            .setEphemeral(true)
                            .queue();
            }
        });
    }

}

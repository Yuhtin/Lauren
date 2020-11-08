package com.yuhtin.lauren.commands.music;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.yuhtin.lauren.core.music.TrackManager;
import com.yuhtin.lauren.models.annotations.CommandHandler;
import com.yuhtin.lauren.models.objects.CommonCommand;
import com.yuhtin.lauren.utils.helper.TrackUtils;
import com.yuhtin.lauren.utils.helper.Utilities;

@CommandHandler(
        name = "bass",
        type = CommandHandler.CommandType.MUSIC,
        description = "Mudar os graves e agudos do meu batidão",
        alias = {"bassboost"}

)
public class BassBoostCommand extends CommonCommand {

    @Override
    protected void executeCommand(CommandEvent event) {

        if (TrackUtils.get().isIdle(event.getTextChannel())) return;
        if (!Utilities.INSTANCE.isDJ(event.getMember(), event.getTextChannel(), true)) return;

        if (event.getArgs().isEmpty()) {
            event.getChannel().sendMessage("<a:tchau:751941650728747140> Você precisa inserir a opção do boost: `low, high, boost ou normal`").queue();
            return;
        }

        switch (event.getArgs().toLowerCase()) {

            case "low":

                TrackManager.get().eqLowBass(-.35f);
                event.getChannel().sendMessage(":loud_sound: Equalizando o baixo da música!").queue();
                break;

            case "high":

                TrackManager.get().eqHighBass(.083f);
                event.getChannel().sendMessage(":loud_sound: Equalizando o grave da música!").queue();
                break;


            case "boost":

                TrackManager.get().bassBoost();
                event.getChannel().sendMessage(":loud_sound: Equalizando tudão, cuidado rapaziada!").queue();
                break;


            case "normal":

                TrackManager.get().eqHighBass(0);
                TrackManager.get().eqLowBass(0);
                event.getChannel().sendMessage(":loud_sound: Tirando equalização").queue();
                break;


            default:
                event.getChannel()
                        .sendMessage(":grey_question: Não encontrei essa equalização, equalizações válidas: `low, high, boost ou normal`")
                        .queue();
        }
    }

}

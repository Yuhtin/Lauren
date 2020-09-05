package com.yuhtin.lauren.commands.music;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.yuhtin.lauren.core.music.AudioInfo;
import com.yuhtin.lauren.core.music.TrackManager;
import com.yuhtin.lauren.models.annotations.CommandHandler;
import com.yuhtin.lauren.service.ConnectionFactory;
import com.yuhtin.lauren.utils.helper.MathUtils;
import com.yuhtin.lauren.utils.helper.TrackUtils;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@CommandHandler(
        name = "playlist",
        type = CommandHandler.CommandType.MUSIC,
        description = "Ver as músicas que eu ainda vou tocar",
        alias = {"queue", "pl"}
)
public class QueueCommand extends Command {

    @Override
    protected void execute(CommandEvent event) {
        if (TrackManager.get().getQueuedTracks().isEmpty()) {
            event.getChannel().sendMessage("\uD83D\uDCCC Eita, não tem nenhum batidão pra tocar, adiciona uns ai <3").queue();
            return;
        }

        StringBuilder builder = new StringBuilder();
        Set<AudioInfo> queue = TrackManager.get().getQueuedTracks();
        long totalTime = 0;

        List<String> users = new ArrayList<>();
        for (AudioInfo audioInfo : queue) {
            builder.append(TrackUtils.get().buildQueueMessage(audioInfo));
            totalTime += audioInfo.getTrack().getInfo().length;

            String userId = audioInfo.getAuthor().getId();
            if (!users.contains(userId)) users.add(userId);
        }

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("\ud83d\udcbf Informações da fila [" + TrackUtils.get().getTimeStamp(totalTime) + "]");

        if (builder.length() <= 2001) {
            users.clear();
            embed.setDescription("\ud83d\udcc0 " + MathUtils.plural(queue.size(), "música", "músicas") + "\n\n" + builder.toString());
            event.getChannel().sendMessage(embed.build()).queue();
            return;
        }

        try {
            String content = builder.toString();

            content = content.replace("`", "");
            for (int i = 0; i < users.size(); i++)
                content = content.replace("(<@" + users.get(i) + ">)", "");

            TrackManager.fields.replace("api_paste_code", content);
            TrackManager.fields.replace("api_paste_name", "Lauren playlist (" + queue.size() + " musicas)");

            ConnectionFactory factory = new ConnectionFactory(TrackManager.fields, "https://pastebin.com/api/api_post.php");
            builder.setLength(1924);
            embed.setDescription("\ud83d\udcc0 " + MathUtils.plural(queue.size(), "música", "músicas") + "\n\n" + builder.toString()
                    + "\n[Clique aqui para ver o resto das músicas](" + factory.buildConnection() + ")");

            event.getChannel().sendMessage(embed.build()).queue();
        } catch (Exception exception) {
            event.getChannel().sendMessage(exception.getMessage()).queue();
            event.getChannel().sendMessage("❌ Eita, algo de errado não está certo, tentei criar um linkzin com as músicas da playlist pra você, mas o hastebin ta off \uD83D\uDE2D").queue();
        }
    }
}

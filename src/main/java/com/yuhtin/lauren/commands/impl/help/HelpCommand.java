package com.yuhtin.lauren.commands.impl.help;

import com.yuhtin.lauren.commands.Command;
import com.yuhtin.lauren.commands.CommandData;
import com.yuhtin.lauren.commands.InfoCacher;
import net.dv8tion.jda.api.EmbedBuilder;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

@CommandData(
        name = "ajuda",
        type = CommandData.CommandType.HELP,
        description = "Informações de comandos do bot",
        alias = {"help"})
public class HelpCommand implements Command {

    @Override
    public void execute(CommandEvent event) {
        String[] args = event.getMessage().getContentRaw().split(" ");

        if (args.length > 1) {

            if (!InfoCacher.getCommands().containsKey(args[1].toLowerCase())) {

                event.getMessage().delete().queue();
                event.getChannel().sendMessage("Hmm, não encontrei o comando `" + args[1] + "` tente usar `$ajuda` para ver meus comandos.")
                        .queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS));
                return;

            }

            EmbedBuilder embed = InfoCacher.getCommands().get(args[1].toLowerCase())
                    .setFooter("Comando usado por " + event.getMember().getEffectiveName(), event.getMember().getUser().getAvatarUrl())
                    .setColor(event.getMember().getColor())
                    .setTimestamp(Instant.now());

            event.getChannel().sendMessageEmbeds(embed.build()).queue();
            return;

        }

        EmbedBuilder helpEmbed = InfoCacher.getHelpEmbed();

        helpEmbed.setFooter("Comando usado por " + event.getMember().getEffectiveName(), event.getMember().getUser().getAvatarUrl())
                .setColor(event.getMember().getColor())
                .setTimestamp(Instant.now());

        event.getChannel().sendMessageEmbeds(helpEmbed.build()).queue();

    }


}

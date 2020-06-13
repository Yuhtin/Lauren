package commands;

import application.Lauren;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import core.RawCommand;
import models.annotations.CommandHandler;
import models.cache.CommandCache;
import net.dv8tion.jda.api.EmbedBuilder;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

@CommandHandler(name = "ajuda", type = CommandHandler.CommandType.HELP, description = "Informações de comandos do bot")
public class HelpCommand extends Command {
    public HelpCommand() {
        this.name = "ajuda";
        this.aliases = new String[]{"help"};
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getMessage().getContentRaw().split(" ");
        if (args.length > 1) {
            if (!CommandCache.commands.containsKey(args[1].toLowerCase())) {
                event.getMessage().delete().queue();
                event.getChannel().sendMessage("Hmm, não encontrei o comando `" + args[1] + "` tente usar `" + Lauren.config.prefix + "ajuda` para ver meus comandos.")
                        .queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS));
                return;
            }
            EmbedBuilder embed = CommandCache.commands.get(args[1].toLowerCase())
                    .setFooter("Comando usado por " + event.getMember().getNickname(), event.getMember().getUser().getAvatarUrl())
                    .setColor(event.getMember().getColor())
                    .setTimestamp(Instant.now());
            event.getChannel().sendMessage(embed.build()).queue();
            return;
        }

        EmbedBuilder helpEmbed = CommandCache.helpEmbed;
        helpEmbed.setFooter("Comando usado por " + event.getMember().getNickname(), event.getMember().getUser().getAvatarUrl())
                .setColor(event.getMember().getColor())
                .setTimestamp(Instant.now());
        event.getChannel().sendMessage(helpEmbed.build()).queue();
    }


}

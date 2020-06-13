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

    public final EmbedBuilder help = new EmbedBuilder();

    public void register() {
        help.setImage("https://i.imgur.com/mQVFSrP.gif")
                .setAuthor("Comandos atacaaaaar \uD83E\uDDF8", "https://google.com", Lauren.bot.getSelfUser().getAvatarUrl())
                .setDescription(
                        "Para mais informações sobre um comando, digite `" + Lauren.config.prefix + "ajuda <comando>` que eu lhe informarei mais sobre ele <a:feliz:712669414566395944>")

                .addField("**Ajuda** ❓ - _Este módulo tem comandos para te ajudar na utilização do bot e do servidor._",
                        getCommands(CommandHandler.CommandType.HELP), false)
                .addField("**Utilidade** \uD83D\uDEE0 - _Este módulo possui coisas úteis pro eu dia a dia._",
                        getCommands(CommandHandler.CommandType.UTILITY), false)
                .addField("**Scrim** \uD83D\uDC7E - _Aqui você pode encontrar comandos relacionados ao meu sistema de partidas_",
                        getCommands(CommandHandler.CommandType.SCRIM), false)

                .addField("__Comandos de Administrador:__", "", false)
                .addField("**Configurações** ⚙ - _Em configurações você define preferências de como agirei em seu servidor_",
                        getCommands(CommandHandler.CommandType.CONFIG), false)
                .addField("**Mensagens Customizadas** \uD83D\uDD79 - _Este módulo possui comandos para você enviar minhas mensagens customizadas._",
                        getCommands(CommandHandler.CommandType.CUSTOM_MESSAGES), false)
                .addField("**Suporte** \uD83E\uDDF0 - _Comandos para dar suporte aos moderadores do servidor_",
                        getCommands(CommandHandler.CommandType.SUPORT), false);
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

        help.setFooter("Comando usado por " + event.getMember().getNickname(), event.getMember().getUser().getAvatarUrl())
                .setColor(event.getMember().getColor())
                .setTimestamp(Instant.now());
        event.getChannel().sendMessage(help.build()).queue();
    }

    private String getCommands(CommandHandler.CommandType commandType) {
        StringBuilder builder = new StringBuilder();
        CommandCache.commandsType.get(commandType).forEach(command -> builder.append("`").append(command.name).append("` "));

        return builder.toString();
    }
}

package com.yuhtin.lauren.commands.impl.help;

import com.yuhtin.lauren.Startup;
import com.yuhtin.lauren.commands.Command;
import com.yuhtin.lauren.commands.CommandInfo;
import com.yuhtin.lauren.commands.InfoCacher;
import com.yuhtin.lauren.startup.Startup;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;

import java.time.Instant;

@CommandInfo(
        name = "ajuda",
        type = CommandType.HELP,
        description = "Informações de comandos do bot",
        args = {
                "[comando]-Comando que deseja ver mais informações"
        }
)
public class HelpCommand implements Command {

    @Override
    public void execute(CommandInteraction event, InteractionHook hook) throws Exception {
        val commandOption = event.getOption("comando");
        val command = commandOption == null ? null : commandOption.getAsString();
        if (command != null) {
            if (!InfoCacher.getInstance().getCommands().containsKey(command)) {
                hook.setEphemeral(true).sendMessage("Hmm, não encontrei o comando `" + command + "` tente usar `/ajuda` para ver meus comandos.").queue();
                return;
            }

            val commandData = InfoCacher.getInstance().getCommands().get(command);
            val embed = new EmbedBuilder()
                    .setImage("https://pa1.narvii.com/7093/1d8551884cec1cb2dd99b88ff4c745436b21f1b4r1-500-500_hq.gif")
                    .setAuthor("Informações do comando " + commandData.name(), null, Startup.getLauren().getJda().getSelfUser().getAvatarUrl())

                    .setDescription("Você está vendo as informações específicas do comando `" + commandData.name() + "`," +
                            " para ver todos os comandos utilize `/ajuda`")

                    .addField("__Informações do comando:__", "", false)
                    .addField("**Nome** ❓ - _Identificador principal do comando_", commandData.name(), false)
                    .addField("**Categoria** \uD83E\uDDE9 - _Categoria do comando_", commandData.type().getName(), false)
                    .addField("**Descrição** ⭐️ - _Pequena descrição do comando_", commandData.description(), false)
                    .setFooter("Comando usado por " + event.getMember().getEffectiveName(), event.getMember().getUser().getAvatarUrl())
                    .setColor(event.getMember().getColor())
                    .setTimestamp(Instant.now()).build();

            hook.setEphemeral(true).sendMessageEmbeds(embed).queue();
            return;

        }

        val helpEmbed = InfoCacher.getInstance().getHelpEmbed();
        helpEmbed.setFooter("Comando usado por " + event.getUser().getName(), event.getUser().getAvatarUrl())
                .setColor(event.getMember().getColor())
                .setTimestamp(Instant.now());

        hook.setEphemeral(true).sendMessageEmbeds(helpEmbed.build()).queue();

    }

}

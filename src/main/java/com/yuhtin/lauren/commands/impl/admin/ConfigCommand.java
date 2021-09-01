package com.yuhtin.lauren.commands.impl.admin;

import com.google.inject.Inject;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.models.annotations.CommandHandler;
import com.yuhtin.lauren.models.objects.Config;
import com.yuhtin.lauren.startup.Startup;
import com.yuhtin.lauren.utils.helper.Utilities;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;

import java.time.Instant;

@CommandHandler(
        name = "config",
        type = CommandHandler.CommandType.CONFIG,
        description = "Configurar algumas informações minha",
        alias = {"configurar", "cfg", "editar", "edit"})
public class ConfigCommand extends Command {

    @Inject private Logger logger;

    @SneakyThrows
    @Override
    protected void execute(CommandEvent event) {
        if (!Utilities.INSTANCE.isPermission(event.getMember(), event.getChannel(), Permission.ADMINISTRATOR, true))
            return;

        String[] arguments = event.getMessage().getContentRaw().split(" ");

        Config config = Startup.getLauren().getConfig();
        if (arguments.length < 2) {

            EmbedBuilder embed = new EmbedBuilder();

            embed.setAuthor("Configurações do bot", null,
                    "https://pt.seaicons.com/wp-content/uploads/2015/07/Settings-L-icon.png");

            embed.setThumbnail(event.getJDA().getSelfUser().getAvatarUrl());
            embed.setColor(event.getMember().getColor());
            embed.setTimestamp(Instant.now());
            embed.setFooter("Comando usado as", event.getAuthor().getAvatarUrl());

            embed.setDescription(
                    "\n" +
                            " **• $config** setprefix <prefixo>\n" +
                            "  Atual: " + config.getPrefix() + "\n" +
                            "  Use para trocar o meu identificador\n\n" +
                            " **• $config** setregistration <messageid>\n" +
                            "  Atual: " + config.getResgistrationId() + "\n" +
                            "  Troque o ID da mensagem de registro\n\n"
            );
            event.getChannel().sendMessage(embed.build()).queue();
            return;
        }

        String value = arguments[2];
        if (arguments[1].equalsIgnoreCase("setprefix")) {

            config.setPrefix(value);

            this.logger.info("The player " + event.getMember().getUser().getName() + " changed the prefix to " + value);
            event.getChannel()
                    .sendMessage("<a:sim:704295025374265387> " +
                            "O meu prefixo foi alterado para '" + value + "'. Reinicie o bot para realizar a troca.")
                    .queue();
            return;
        }

        if (arguments[1].equalsIgnoreCase("setregistration")) {
            try {

                config.setResgistrationId(Long.parseLong(value));
                this.logger.info("The player " + event.getMember().getUser().getName() + " changed the registrationID to " + value);

            } catch (Exception exception) {

                event.getChannel()
                        .sendMessage("<a:nao:704295026036834375> " +
                                "O valor inserido é invalido: '" + value + "' (insira um id).")
                        .queue();
                return;

            }

            event.getChannel()
                    .sendMessage("<a:sim:704295025374265387> " +
                            "O id da mensagem de registro foi alterado para '" + value + "'.")
                    .queue();
            return;

        }

    }
}

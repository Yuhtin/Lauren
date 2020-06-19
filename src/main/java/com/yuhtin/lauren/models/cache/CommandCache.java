package com.yuhtin.lauren.models.cache;

import com.yuhtin.lauren.application.Lauren;
import com.yuhtin.lauren.core.RawCommand;
import com.yuhtin.lauren.models.annotations.CommandHandler;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandCache {

    public static final Map<String, EmbedBuilder> commands = new HashMap<>();
    public static final Map<CommandHandler.CommandType, List<RawCommand>> commandsType = new HashMap<>();
    public static EmbedBuilder helpEmbed;

    public static void start() {
        for (CommandHandler.CommandType value : CommandHandler.CommandType.values()) {
            commandsType.put(value, new ArrayList<>());
        }
    }

    public static void insert(CommandHandler.CommandType type, RawCommand rawCommand) {
        commands.put(rawCommand.name.toLowerCase(), new EmbedBuilder()
                .setImage("https://pa1.narvii.com/7093/1d8551884cec1cb2dd99b88ff4c745436b21f1b4r1-500-500_hq.gif")
                .setAuthor("Informações do comando " + rawCommand.name, "https://google.com", Lauren.bot.getSelfUser().getAvatarUrl())
                .setDescription("Você está vendo as informações específicas do comando `" + rawCommand.name + "`," +
                        " para ver todos os comandos utilize `" + Lauren.config.prefix + "ajuda <comando>`")

                .addField("__Informações do comando:__", "", false)
                .addField("**Nome** ❓ - _Identificador principal do comando_", rawCommand.name, false)
                .addField("**Categoria** \uD83E\uDDE9 - _Categoria do comando_", rawCommand.type.name, false)
                .addField("**Descrição** ⭐️ - _Pequena descrição do comando_", rawCommand.description, false));

        commandsType.get(type).add(rawCommand);
    }

    public static void makeEmbed() {
        helpEmbed = new EmbedBuilder().setImage("https://i.imgur.com/mQVFSrP.gif")
                .setAuthor("Comandos atacaaaaar \uD83E\uDDF8", "https://google.com", Lauren.bot.getSelfUser().getAvatarUrl())
                .setDescription(
                        "Para mais informações sobre um comando, digite `" + Lauren.config.prefix + "ajuda <comando>` que eu lhe informarei mais sobre ele <a:feliz:712669414566395944>")

                .addField("**Ajuda** ❓ - _Este módulo tem comandos para te ajudar na utilização do bot e do servidor._",
                        getCommands(CommandHandler.CommandType.HELP), false)
                .addField("**Música** \uD83C\uDFB6 - _Comandos relacionados ao meu sistema de tocar batidões._",
                        getCommands(CommandHandler.CommandType.MUSIC), false)
                .addField("**Utilidade** \uD83D\uDEE0 - _Este módulo possui coisas úteis pro eu dia a dia._",
                        getCommands(CommandHandler.CommandType.UTILITY), false)
                .addField("**Scrim** \uD83D\uDC7E - _Aqui você pode encontrar comandos relacionados ao meu sistema de partidas._",
                        getCommands(CommandHandler.CommandType.SCRIM), false)

                .addField("__Comandos de Administrador:__", "", false)
                .addField("**Configurações** ⚙ - _Em configurações você define preferências de como agirei em seu servidor._",
                        getCommands(CommandHandler.CommandType.CONFIG), false)
                .addField("**Mensagens Customizadas** \uD83D\uDD79 - _Este módulo possui algumas de minhas mensagens customizadas._",
                        getCommands(CommandHandler.CommandType.CUSTOM_MESSAGES), false)
                .addField("**Suporte** \uD83E\uDDF0 - _Comandos para dar suporte aos moderadores do servidor._",
                        getCommands(CommandHandler.CommandType.SUPORT), false);
    }

    private static String getCommands(CommandHandler.CommandType commandType) {
        StringBuilder builder = new StringBuilder();
        commandsType.get(commandType).forEach(command -> builder.append("`").append(command.name).append("` "));

        return builder.toString();
    }
}

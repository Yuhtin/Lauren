package com.yuhtin.lauren.service;

import com.yuhtin.lauren.Lauren;
import com.yuhtin.lauren.core.entities.RawCommand;
import com.yuhtin.lauren.models.annotations.CommandHandler;
import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.*;

public class CommandCache {

    private CommandCache() {}

    protected static final Map<CommandHandler.CommandType, List<RawCommand>> commandsType = new EnumMap<> (CommandHandler.CommandType.class);

    @Getter private static final Map<String, EmbedBuilder> commands = new HashMap<>();
    @Getter private static final EmbedBuilder helpEmbed = new EmbedBuilder();
    @Getter private static final List<String> aliases = new ArrayList<>();

    public static void start() {
        for (CommandHandler.CommandType value : CommandHandler.CommandType.values()) {
            commandsType.put(value, new ArrayList<>());
        }
    }

    public static void insert(CommandHandler.CommandType type, RawCommand rawCommand) {
        aliases.add(rawCommand.name);
        aliases.addAll(Arrays.asList(rawCommand.aliases));

        commands.put(rawCommand.name.toLowerCase(), new EmbedBuilder()
                .setImage("https://pa1.narvii.com/7093/1d8551884cec1cb2dd99b88ff4c745436b21f1b4r1-500-500_hq.gif")
                .setAuthor("Informações do comando " + rawCommand.name, null,
                        Lauren.getInstance()
                                .getBot()
                                .getSelfUser()
                                .getAvatarUrl())

                .setDescription("Você está vendo as informações específicas do comando `" + rawCommand.name + "`," +
                        " para ver todos os comandos utilize `$ajuda <comando>`")

                .addField("__Informações do comando:__", "", false)
                .addField("**Nome** ❓ - _Identificador principal do comando_", rawCommand.name, false)
                .addField("**Categoria** \uD83E\uDDE9 - _Categoria do comando_", rawCommand.type.getName(), false)
                .addField("**Descrição** ⭐️ - _Pequena descrição do comando_", rawCommand.description, false));

        commandsType.get(type).add(rawCommand);
    }

    public static void construct() {
        helpEmbed.setImage("https://i.imgur.com/mQVFSrP.gif");

        helpEmbed.setAuthor("Comandos atacaaaaar \uD83E\uDDF8", null,
                Lauren.getInstance()
                        .getBot()
                        .getSelfUser()
                        .getAvatarUrl());

        helpEmbed.setDescription(
                "Para mais informações sobre um comando, digite `$ajuda <comando>` que eu lhe informarei mais sobre ele " +
                        "<a:feliz:712669414566395944>");

        helpEmbed.addField("**Ajuda** ❓ - _Este módulo tem comandos para te ajudar na utilização do bot e do servidor._",
                getCommands(CommandHandler.CommandType.HELP), false);

        helpEmbed.addField("**Música** \uD83C\uDFB6 - _Comandos relacionados ao meu sistema de tocar batidões._",
                getCommands(CommandHandler.CommandType.MUSIC), false);

        helpEmbed.addField("**Utilidade** \uD83D\uDEE0 - _Este módulo possui coisas úteis pro eu dia a dia._",
                getCommands(CommandHandler.CommandType.UTILITY), false);

        helpEmbed.addField("**Scrim** \uD83D\uDC7E - _Aqui você pode encontrar comandos relacionados ao meu sistema de partidas._",
                getCommands(CommandHandler.CommandType.SCRIM), false);

        helpEmbed.addField("**Outros** \uD83D\uDC7E - _Aqui você pode encontrar comandos sem categoria._",
                getCommands(CommandHandler.CommandType.OTHER), false);

        helpEmbed.addField("__Comandos de Administrador:__", "", false);

        helpEmbed.addField("**Configurações** ⚙ - _Em configurações você define preferências de como agirei em seu servidor._",
                getCommands(CommandHandler.CommandType.CONFIG), false);

        helpEmbed.addField("**Mensagens Customizadas** \uD83D\uDD79 - _Este módulo possui algumas de minhas mensagens customizadas._",
                getCommands(CommandHandler.CommandType.CUSTOM_MESSAGES), false);

        helpEmbed.addField("**Suporte** \uD83E\uDDF0 - _Comandos para dar suporte aos moderadores do servidor._",
                getCommands(CommandHandler.CommandType.ADMIN), false);

        commandsType.clear();
    }

    private static String getCommands(CommandHandler.CommandType commandType) {
        StringBuilder builder = new StringBuilder();
        commandsType.get(commandType).forEach(command -> builder.append("`").append(command.name).append("` "));

        return builder.toString();
    }
}
package models.cache;

import application.Lauren;
import core.RawCommand;
import models.annotations.CommandHandler;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandCache {

    public static Map<String, EmbedBuilder> commands = new HashMap<>();
    public static Map<CommandHandler.CommandType, List<RawCommand>> commandsType = new HashMap<>();

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
}

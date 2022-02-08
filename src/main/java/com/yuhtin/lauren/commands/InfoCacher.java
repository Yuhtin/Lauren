package com.yuhtin.lauren.commands;

import com.yuhtin.lauren.startup.Startup;
import com.yuhtin.lauren.utils.lazy.LazyInstance;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.*;

@NoArgsConstructor
public class InfoCacher {

    private static final LazyInstance<InfoCacher> LAZY_INSTANCE = new LazyInstance<>();

    private final Map<CommandData.CommandType, List<String>> commandByType = new EnumMap<> (CommandData.CommandType.class);

    @Getter private final Map<String, EmbedBuilder> commands = new HashMap<>();
    @Getter private final EmbedBuilder helpEmbed = new EmbedBuilder();

    public static InfoCacher getInstance() {
        return LAZY_INSTANCE.getOrCompute(InfoCacher::new);
    }

    public void start() {
        for (CommandData.CommandType value : CommandData.CommandType.values()) {
            commandByType.put(value, new ArrayList<>());
        }
    }

    public void insert(CommandData commandData) {
        commands.put(commandData.name(), new EmbedBuilder()
                .setImage("https://pa1.narvii.com/7093/1d8551884cec1cb2dd99b88ff4c745436b21f1b4r1-500-500_hq.gif")
                .setAuthor("Informações do comando " + commandData.name(), null, Startup.getLauren().getBot().getSelfUser().getAvatarUrl())

                .setDescription("Você está vendo as informações específicas do comando `" + commandData.name() + "`," +
                        " para ver todos os comandos utilize `$ajuda <comando>`")

                .addField("__Informações do comando:__", "", false)
                .addField("**Nome** ❓ - _Identificador principal do comando_", commandData.name(), false)
                .addField("**Categoria** \uD83E\uDDE9 - _Categoria do comando_", commandData.type().getName(), false)
                .addField("**Descrição** ⭐️ - _Pequena descrição do comando_", commandData.description(), false));

        commandByType.get(commandData.type()).add(commandData.name());
    }

    public void construct() {
        helpEmbed.setImage("https://i.imgur.com/mQVFSrP.gif")
                .setAuthor("Comandos atacaaaaar \uD83E\uDDF8", null, Startup.getLauren().getBot().getSelfUser().getAvatarUrl())
                .setDescription("Para mais informações sobre um comando, digite `$ajuda <comando>` que eu lhe informarei mais sobre ele <a:feliz:712669414566395944>");

        for (CommandData.CommandType value : CommandData.CommandType.values()) {
            val commandInfo = String.format("**%s** %s - _%s_", value.getName(), value.getEmoji(), value.getDescription());
            val commands = getCommands(value);

            helpEmbed.addField(commandInfo, commands, false);
        }

        commandByType.clear();
    }

    private String getCommands(CommandData.CommandType commandType) {
        val builder = new StringBuilder();
        commandByType.get(commandType).forEach(command -> builder.append("`").append(command).append("` "));

        return builder.toString();
    }
}
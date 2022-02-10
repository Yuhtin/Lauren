package com.yuhtin.lauren.commands;

import com.yuhtin.lauren.startup.Startup;
import com.yuhtin.lauren.utils.LazyInstance;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.*;

@NoArgsConstructor
public class InfoCacher {

    private static final LazyInstance<InfoCacher> LAZY_INSTANCE = new LazyInstance<>();

    private final Map<CommandData.CommandType, List<String>> commandByType = new EnumMap<>(CommandData.CommandType.class);
    @Getter private final Map<String, CommandData> commands = new HashMap<>();

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
        commands.put(commandData.name(), commandData);
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
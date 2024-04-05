package com.yuhtin.lauren.commands;

import com.yuhtin.lauren.startup.Startup;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.*;

@NoArgsConstructor
public class InfoCacher {

    private static final LazyInstance<InfoCacher> LAZY_INSTANCE = new LazyInstance<>();

    private final Map<CommandInfo.CommandType, List<String>> commandByType = new EnumMap<>(CommandInfo.CommandType.class);
    @Getter private final Map<String, CommandInfo> commands = new HashMap<>();

    @Getter private final EmbedBuilder helpEmbed = new EmbedBuilder();

    public static InfoCacher getInstance() {
        return LAZY_INSTANCE.getOrCompute(InfoCacher::new);
    }

    public void start() {
        for (CommandInfo.CommandType value : CommandInfo.CommandType.values()) {
            commandByType.put(value, new ArrayList<>());
        }
    }

    public void insert(CommandInfo commandInfo) {
        commands.put(commandInfo.name(), commandInfo);
        commandByType.get(commandInfo.type()).add(commandInfo.name());
    }

    public void construct() {
        helpEmbed.setImage("https://i.imgur.com/mQVFSrP.gif")
                .setAuthor("Comandos atacaaaaar \uD83E\uDDF8", null, Startup.getLauren().getBot().getSelfUser().getAvatarUrl())
                .setDescription("Para mais informações sobre um comando, digite `/ajuda` que eu lhe informarei mais sobre ele <a:feliz:712669414566395944>");

        for (CommandInfo.CommandType value : CommandInfo.CommandType.values()) {
            val commandInfo = String.format("**%s** %s - _%s_", value.getName(), value.getEmoji(), value.getDescription());
            val commands = getCommands(value);

            helpEmbed.addField(commandInfo, commands, false);
        }

        commandByType.clear();
    }

    private String getCommands(CommandInfo.CommandType commandType) {
        val builder = new StringBuilder();
        commandByType.get(commandType).forEach(command -> builder.append("`").append(command).append("` "));

        return builder.toString();
    }
}
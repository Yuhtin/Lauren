package com.yuhtin.lauren.commands;

import com.yuhtin.lauren.Startup;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.*;

@Getter
@NoArgsConstructor
public class InfoCacher {

    @Getter private final static InfoCacher instance = new InfoCacher();
    
    private final Map<CommandType, List<String>> commandByType = new EnumMap<>(CommandType.class);
    private final Map<String, CommandInfo> commands = new HashMap<>();

    private final EmbedBuilder helpEmbed = new EmbedBuilder();
    
    public void start() {
        for (CommandType value : CommandType.values()) {
            commandByType.put(value, new ArrayList<>());
        }
    }

    public void insert(CommandInfo commandInfo) {
        commands.put(commandInfo.name(), commandInfo);
        commandByType.get(commandInfo.type()).add(commandInfo.name());
    }

    public void construct() {
        helpEmbed.setImage("https://i.imgur.com/mQVFSrP.gif")
                .setAuthor("Comandos atacaaaaar \uD83E\uDDF8", null, Startup.getLauren().getJda().getSelfUser().getAvatarUrl())
                .setDescription("Para mais informações sobre um comando, digite `/ajuda` que eu lhe informarei mais sobre ele <a:feliz:712669414566395944>");

        for (CommandType value : CommandType.values()) {
            String commandInfo = String.format("**%s** %s - _%s_", value.getName(), value.getEmoji(), value.getDescription());
            String commands = getCommands(value);

            helpEmbed.addField(commandInfo, commands, false);
        }

        commandByType.clear();
    }

    private String getCommands(CommandType commandType) {
        StringBuilder builder = new StringBuilder();
        commandByType.get(commandType).forEach(command -> builder.append("`").append(command).append("` "));

        return builder.toString();
    }

}
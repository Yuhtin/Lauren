package com.yuhtin.lauren.commands;

import com.yuhtin.lauren.Lauren;
import com.yuhtin.lauren.module.Module;
import com.yuhtin.lauren.util.EmbedUtil;
import com.yuhtin.lauren.util.LoggerUtil;
import com.yuhtin.lauren.util.PathFinder;
import lombok.val;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.logging.Logger;

public class CommandModule implements Module {

    private final Map<String, Command> commands = new HashMap<>();

    @Override
    public boolean setup(Lauren lauren) {
        Logger logger = lauren.getLogger();

        InfoCacher infoCacher = InfoCacher.getInstance();
        infoCacher.start();

        HashMap<String, CommandDataImpl> hashMap = new HashMap<>();
        for (Class castedClass : PathFinder.from("com.yuhtin.lauren.commands.impl")) {
            try {
                Object object = castedClass.newInstance();

                if (castedClass.isAnnotationPresent(CommandInfo.class)) {
                    Command command = (Command) object;
                    CommandInfo data = (CommandInfo) castedClass.getAnnotation(CommandInfo.class);

                    var commandName = data.name();
                    logger.config("Registering command: " + commandName);

                    register(commandName, command);

                    var subCommand = "";
                    if (commandName.contains(".")) {
                        val split = commandName.split("\\.");
                        commandName = split[0];
                        subCommand = split[1];
                    }

                    CommandDataImpl defaultData = new CommandDataImpl(commandName, data.description());
                    CommandDataImpl currentData = hashMap.getOrDefault(commandName, defaultData);

                    if (!subCommand.equalsIgnoreCase("")) {
                        SubcommandData subcommandData = new SubcommandData(subCommand, data.description());
                        currentData.addSubcommands(subcommandData);

                        populateOptions(data, null, subcommandData);
                    } else {
                        List<Permission> permissions = new ArrayList<>();
                        Collections.addAll(permissions, data.permissions());

                        if (!permissions.isEmpty()) {
                            currentData.setDefaultPermissions(DefaultMemberPermissions.enabledFor(data.permissions()));
                        }

                        populateOptions(data, currentData, null);
                    }

                    hashMap.put(commandName, currentData);
                    infoCacher.insert(data);
                } else {
                    throw new InstantiationException();
                }
            } catch (Exception exception) {
                logger.severe("The " + castedClass.getName() + " class could not be instantiated");
                LoggerUtil.printException(exception);
            }
        }

        infoCacher.construct();

        for (Guild guild : lauren.getJda().getGuilds()) {
            guild.updateCommands().addCommands(hashMap.values()).queue();
        }

        logger.info("Registered " + hashMap.size() + " commands successfully");
        return true;
    }

    public void register(String key, Command value) {
        commands.put(key, value);
    }

    @SubscribeEvent
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getChannel().getType() != ChannelType.TEXT) {
            event.replyEmbeds(EmbedUtil.create("Você só pode usar meus comandos em servidores.")).queue();
            return;
        }

        String name = event.getName();
        String subcommandName = event.getSubcommandName();
        if (subcommandName != null) {
            name += "." + subcommandName;
        }

        Logger logger = LoggerUtil.getLogger();
        Command command = commands.getOrDefault(name, null);
        if (command == null) {
            logger.info("Não encontrei o comando " + name);
            return;
        }

        event.deferReply().queue(hook -> {
            try {
                command.execute(event, hook);
            } catch (Exception exception) {
                exception.printStackTrace();
                hook.sendMessage("ERRO!").queue();
            }
        });
    }

    private void populateOptions(CommandInfo handler, CommandDataImpl commandData, SubcommandData subcommandData) {
        for (String option : handler.args()) {
            String[] split = option.split("-");
            String argName = split[0];
            OptionType optionType;

            if (argName.contains("@")) optionType = OptionType.USER;
            else if (argName.contains("%")) optionType = OptionType.ROLE;
            else if (argName.contains("#")) optionType = OptionType.CHANNEL;
            else if (argName.contains("!")) optionType = OptionType.INTEGER;
            else optionType = OptionType.STRING;

            String reformatedName = argName.replace("[", "")
                    .replace("]", "")
                    .replace("!", "")
                    .replace("<", "")
                    .replace(">", "")
                    .replace("@", "")
                    .replace("%", "")
                    .replace("#", "");

            OptionData optionData = new OptionData(
                    optionType,
                    reformatedName,
                    split[1],
                    argName.contains("<")
            );

            if (commandData != null) commandData.addOptions(optionData);
            if (subcommandData != null) subcommandData.addOptions(optionData);
        }
    }

}

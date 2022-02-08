package com.yuhtin.lauren.commands;

import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;

public interface Command {

    void execute(CommandInteraction command, InteractionHook hook) throws Exception;

}


package com.yuhtin.lauren.commands;

import net.dv8tion.jda.api.entities.Message;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
public interface Command {

    void execute(Message message, String args);

}

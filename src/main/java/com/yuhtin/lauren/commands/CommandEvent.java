package com.yuhtin.lauren.commands;

import lombok.Builder;
import lombok.Data;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
@Data
@Builder
public class CommandEvent {

    private final TextChannel channel;
    private final Message message;

    private final Member member;
    private final User author;

}

package com.yuhtin.lauren.commands.impl.admin;

import com.google.inject.Inject;
import com.yuhtin.lauren.commands.Command;
import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.commands.CommandInfo;
import com.yuhtin.lauren.utils.UserUtil;
import lombok.val;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;

import java.util.List;

@CommandInfo(
        name = "clearchat",
        type = CommandInfo.CommandType.ADMIN,
        description = "Limpar algumas mensagens do canal atual",
        args = {
                "<!quantity>-Quantidade de mensagens para apagar",
                "[@user]-Mencione um usuário para apagar as mensagens só dele"
        }
)
public class ClearCommand implements Command {

    @Inject private Logger logger;

    @Override
    public void execute(CommandInteraction event, InteractionHook hook) throws Exception {
        if (event.getMember() == null || !UserUtil.hasPermission(event.getMember(), hook, Permission.MESSAGE_MANAGE)) return;

        long id = 0L;
        val userOption = event.getOption("user");
        if (userOption != null) id = userOption.getAsMember().getIdLong();

        val purge = (int) event.getOption("quantity").getAsDouble();
        if (purge > 100) {
            hook.sendMessage("❌ O limite é de 100 mensagens por vez.").queue();
            return;
        }

        MessageHistory messageHistory = new MessageHistory(event.getChannel());

        long finalId = id;
        messageHistory.retrievePast(purge).queue(messages -> {
            int cleared = 0;
            for (Message message : messages) {
                if (message == null || (finalId != 0L && message.getAuthor().getIdLong() != finalId)) continue;

                logger.info(String.format("User %s (%s) cleared message from %s (%s): %s",
                        event.getUser().getAsTag(),
                        event.getUser().getId(),
                        message.getAuthor().getAsTag(),
                        message.getAuthor().getId(),
                        message.getContentDisplay()
                ));

                message.delete().queue();
                ++cleared;
            }

            hook.sendMessage("<:online:703089222021808170> Foram apagadas **" + cleared + "** mensagens deste canal.").queue();
        });
    }

}

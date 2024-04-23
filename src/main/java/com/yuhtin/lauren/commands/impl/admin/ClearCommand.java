package com.yuhtin.lauren.commands.impl.admin;

import com.yuhtin.lauren.commands.Command;
import com.yuhtin.lauren.commands.CommandInfo;
import com.yuhtin.lauren.commands.CommandType;
import com.yuhtin.lauren.util.LoggerUtil;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

@CommandInfo(
        name = "clearchat",
        type = CommandType.ADMIN,
        description = "Limpar algumas mensagens do canal atual",
        args = {
                "<!quantity>-Quantidade de mensagens para apagar",
                "[@user]-Mencione um usuário para apagar as mensagens só dele"
        },
        permissions = { Permission.MESSAGE_MANAGE }
)
public class ClearCommand implements Command {

    @Override
    public void execute(CommandInteraction event, InteractionHook hook) throws Exception {
        if (event.getMember() == null) return;

        long id = 0L;
        OptionMapping userOption = event.getOption("user");
        if (userOption != null) id = userOption.getAsMember().getIdLong();

        int purge = event.getOption("quantity").getAsInt();
        if (purge > 100) {
            hook.sendMessage("❌ O limite é de 100 mensagens por vez.").queue();
            return;
        }

        MessageHistory messageHistory = new MessageHistory(event.getMessageChannel());

        long finalId = id;
        messageHistory.retrievePast(purge).queue(messages -> {
            int cleared = 0;
            for (Message message : messages) {
                if (message == null || (finalId != 0L && message.getAuthor().getIdLong() != finalId)) continue;

                LoggerUtil.getLogger().info(String.format("User %s (%s) cleared message from %s (%s): %s",
                        event.getUser().getName(),
                        event.getUser().getId(),
                        message.getAuthor().getName(),
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

package com.yuhtin.lauren.commands.impl.admin;

import com.google.inject.Inject;
import com.yuhtin.lauren.commands.Command;
import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.commands.CommandData;
import com.yuhtin.lauren.utils.helper.UserUtil;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

import java.util.List;
import java.util.concurrent.TimeUnit;

@CommandData(
        name = "clear",
        type = CommandData.CommandType.ADMIN,
        description = "Limpar algumas mensagens do canal atual",
        alias = {"clearchat", "cc"})
public class ClearCommand implements Command {

    @Inject private Logger logger;

    @Override
    public void execute(CommandEvent event) {

        if (!UserUtil.hasPermission(event.getMember(), event.getMessage(), Permission.MESSAGE_MANAGE, true)) return;

        String[] args = event.getMessage().getContentRaw().split(" ");
        if (args.length < 2) {
            MessageAction message = event.getChannel().sendMessage("❌ Utilize $clear <numero> (mention).");
            message.queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS));
            return;
        }

        int purge = Integer.parseInt(args[1]);
        if (purge > 100) {
            MessageAction message = event.getChannel().sendMessage("❌ O limite é de 100 mensagens por vez.");
            message.queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS));
            return;
        }

        long id = 0L;

        if (args.length > 2)
            id = event.getMessage().getMentionedMembers().get(0).getIdLong();

        MessageHistory messageHistory = new MessageHistory(event.getChannel());
        List<Message> messages;

        messages = messageHistory.retrievePast(purge).complete();
        int cleared = 0;
        for (Message message : messages) {
            if (message == null || (id != 0L && message.getAuthor().getIdLong() != id)) continue;

            event.getChannel().deleteMessageById(message.getId()).queue();
            ++cleared;
        }

        event.getChannel().sendMessage("<:online:703089222021808170> Foram apagadas **" + cleared + "** mensagens deste canal.")
                .queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS));

        this.logger.info("The user " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator()
                + " cleared " + cleared + " messages from channel #" + event.getChannel().getName()
                + " (" + event.getChannel().getId() + ")");
    }
}

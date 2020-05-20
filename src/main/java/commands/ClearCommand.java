package commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import logger.Logger;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import utils.helper.Utilities;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class ClearCommand extends Command {
    public ClearCommand() {
        this.name = "clear";
        this.aliases = new String[]{"clearchat", "cc"};
        this.help = "Limpar mensagens no chat.";
    }

    @Override
    protected void execute(CommandEvent event) {

        if (!Utilities.isPermission(event.getMember(), event.getChannel(), Permission.MESSAGE_MANAGE)) return;

        String[] args = event.getMessage().getContentRaw().split(" ");
        if (args.length < 2) {
            MessageAction message = event.getChannel().sendMessage("❌ Utilize $clear <numero> (mention).");
            message.queue((m) -> m.delete().queueAfter(5, TimeUnit.SECONDS));
            return;
        }

        int purge = Integer.parseInt(args[1]);
        long id = 0L;

        if (args.length > 2) {
            id = event.getMessage().getMentionedMembers().get(0).getIdLong();
            event.getMessage().delete().queue();
        }

        MessageHistory messageHistory = new MessageHistory(event.getChannel());
        List<Message> messages;

        messages = messageHistory.retrievePast(purge).complete();
        int cleared = 0;
        for (Message message : messages) {
            if (message == null) continue;
            if (id != 0L && message.getAuthor().getIdLong() != id) continue;
            event.getChannel().deleteMessageById(message.getId()).queue();
            ++cleared;
        }
        MessageAction message = event.getChannel().sendMessage("<:online:703089222021808170> Foram apagadas **" + cleared + "** mensagens deste canal.");
        message.queue((m) -> m.delete().queueAfter(5, TimeUnit.SECONDS));

        Logger.log("The user " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator()
                + " cleared " + cleared + " messages from channel #" + event.getChannel().getName()
                + " (" + event.getChannel().getId() + ")").save();
    }
}

package commands.messages;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import lombok.SneakyThrows;
import models.annotations.CommandHandler;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import utils.helper.Utilities;

import java.io.File;
import java.util.concurrent.TimeUnit;

@CommandHandler(name = "sendmessage", type = CommandHandler.CommandType.CUSTOM_MESSAGES, description = "Enviar a mensagem contida como argumento do comando")
public class SendMessageCommand extends Command {

    public SendMessageCommand() {
        this.name = "sendmessage";
    }

    @SneakyThrows
    @Override
    protected void execute(CommandEvent event) {
        if (!Utilities.isPermission(event.getMember(), event.getChannel(), Permission.MANAGE_CHANNEL)) return;

        if (!event.getMessage().getAttachments().isEmpty()) {
            Message.Attachment attachment = event.getMessage().getAttachments().get(0);
            try {
                File file = Utilities.getAttachment(attachment);

                if (file != null) event.getChannel().sendMessage("@everyone").addFile(file)
                        .queue(m -> event.getMessage().delete().queueAfter(3, TimeUnit.SECONDS, m2 -> file.delete()));
            } catch (Exception exception) {}
        }
        event.getChannel().sendMessage(event.getArgs()).queue();
    }
}

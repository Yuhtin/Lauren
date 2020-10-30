package com.yuhtin.lauren.commands.admin.punishiment;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.yuhtin.lauren.models.annotations.CommandHandler;
import com.yuhtin.lauren.utils.helper.Utilities;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;

@CommandHandler(
        name = "ban",
        type = CommandHandler.CommandType.ADMIN,
        description = "Lançar o machado do ban",
        alias = {}
)
public class BanCommand extends Command {

    @Override
    protected void execute(CommandEvent event) {
        if (!Utilities.INSTANCE.isPermission(event.getMember(), event.getChannel(), Permission.ADMINISTRATOR, true))
            return;

        if (event.getMessage().getMentionedMembers().isEmpty()) {
            event.getChannel().sendMessage("<a:nao:704295026036834375> Você precisa mencionar um jogador para banir, exemplo `$ban @Yuhtin`").queue();
            return;
        }

        Member target = event.getMessage().getMentionedMembers().get(0);
        target.ban(7, "Banned from " + Utilities.INSTANCE.getFullName(event.getAuthor())).queue();

        event.getChannel().sendMessage("<:feliz_pra_caralho:760202116504485948> Você baniu o jogador `" + Utilities.INSTANCE.getFullName(target.getUser()) + "` com sucesso.").queue();
    }
}

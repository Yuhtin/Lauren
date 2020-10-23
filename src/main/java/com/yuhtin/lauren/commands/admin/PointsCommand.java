package com.yuhtin.lauren.commands.admin;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.yuhtin.lauren.models.annotations.CommandHandler;
import com.yuhtin.lauren.utils.helper.Utilities;
import net.dv8tion.jda.api.Permission;

@CommandHandler(
        name = "points",
        type = CommandHandler.CommandType.ADMIN,
        description = "Mudar algumas coisas nos pontos dos outros",
        alias = {"configpoints"}
)
public class PointsCommand extends Command {

    @Override
    protected void execute(CommandEvent event) {
        if (!Utilities.INSTANCE.isPermission(event.getMember(), event.getChannel(), Permission.ADMINISTRATOR, true))
            return;


        String[] arguments = event.getArgs().split(" ");
        if (arguments.length < 5) {
            event.getChannel().sendMessage("<:chorano:726207542413230142> Uso incorreto, formato certo: `$points <set, add, remove> @Yuhtin <Valorant ou 8Ball> <pontos>").queue();
            return;
        }

        
    }
}

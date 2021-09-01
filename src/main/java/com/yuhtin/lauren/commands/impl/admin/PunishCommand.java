package com.yuhtin.lauren.commands.impl.admin;

import com.google.inject.Inject;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.yuhtin.lauren.core.punish.PunishmentRule;
import com.yuhtin.lauren.manager.PunishmentManager;
import com.yuhtin.lauren.commands.CommandHandler;
import com.yuhtin.lauren.utils.helper.Utilities;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;

@CommandHandler(
        name = "punir",
        type = CommandHandler.CommandType.ADMIN,
        description = "Lançar o machado do ban",
        alias = {}
)
public class PunishCommand extends Command {

    @Inject private PunishmentManager punishmentManager;

    @Override
    protected void execute(CommandEvent event) {
        if (!Utilities.INSTANCE.isPermission(event.getMember(), event.getChannel(), Permission.MESSAGE_MANAGE, true))
            return;

        String[] arguments = event.getArgs().split(" ");

        // for administrator does not need proof
        if (arguments.length < 2 || (arguments.length < 3 && !event.getMember().hasPermission(Permission.ADMINISTRATOR))) {
            event.getChannel().sendMessage("<a:nao:704295026036834375> Você precisa mencionar um jogador para banir, exemplo `$punir @Yuhtin <regra> [prova]`").queue();
            return;
        }

        Member target = event.getMessage().getMentionedMembers().get(0);
        String proof = arguments.length < 3 ? "" : arguments[2];

        String rule = arguments[1];
        if (!rule.contains(".")) {
            event.getChannel().sendMessage(":x: Esta regra não é valida, exemplo de regra valida: `1.1`").queue();
            return;
        }

        try {

            PunishmentRule punishmentRule = PunishmentRule.valueOf("P" + rule.replace(".", ""));
            this.punishmentManager.applyPunish(event.getAuthor(), target, punishmentRule, proof);

            event.getChannel()
                    .sendMessage("<:feliz_pra_caralho:760202116504485948> " +
                            "Você puniu o jogador `" + Utilities.INSTANCE.getFullName(target.getUser()) + "` com sucesso.")
                    .queue();

        }catch (IllegalStateException exception) {
            event.getChannel().sendMessage(":x: Esta regra não existe, tente novamente").queue();
        }
    }
}

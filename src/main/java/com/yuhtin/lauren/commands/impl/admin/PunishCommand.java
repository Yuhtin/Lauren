package com.yuhtin.lauren.commands.impl.admin;

import com.google.inject.Inject;
import com.yuhtin.lauren.commands.Command;
import com.yuhtin.lauren.commands.CommandInfo;
import com.yuhtin.lauren.core.punish.PunishmentRule;
import com.yuhtin.lauren.module.impl.misc.PunishmentModule;
import lombok.val;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;

@CommandInfo(
        name = "punir",
        type = CommandInfo.CommandType.ADMIN,
        description = "Lançar o machado do ban",
        args = {
                "<@user>-Usuário a ser punido",
                "<rule>-Regra que indica a infração que o jogador cometeu",
                "[proof]-Prova de infração do jogador"
        }
)
public class PunishCommand implements Command {

    @Inject
    private PunishmentModule punishmentModule;

    @Override
    public void execute(CommandInteraction event, InteractionHook hook) throws Exception {
        if (!UserUtil.hasPermission(event.getMember(), hook, Permission.MESSAGE_MANAGE)) return;

        val target = event.getOption("user").getAsMember();
        val rule = event.getOption("rule").getAsString();
        val proofOption = event.getOption("proof");

        if (proofOption == null && !event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
            hook.sendMessage("<a:nao:704295026036834375> Você precisa inserir uma prova de infração.`").queue();
            return;
        }

        val proof = proofOption.getAsString();
        if (!rule.contains(".")) {
            hook.sendMessage(":x: Esta regra não é valida, exemplo de regra valida: `1.1`").queue();
            return;
        }

        try {
            val punishmentRule = PunishmentRule.valueOf("P" + rule.replace(".", ""));
            punishmentModule.applyPunish(event.getUser(), target, punishmentRule, proof);

            hook.setEphemeral(true)
                    .sendMessage("<:feliz_pra_caralho:760202116504485948> " +
                            "Você puniu o jogador `" + target.getUser().getName() + "` com sucesso.")
                    .queue();

        } catch (IllegalStateException exception) {
            hook.sendMessage(":x: Esta regra não existe, tente novamente").queue();
        }
    }
}

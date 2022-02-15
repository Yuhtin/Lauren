package com.yuhtin.lauren.commands.impl.messages;

import com.yuhtin.lauren.commands.Command;
import com.yuhtin.lauren.commands.CommandInfo;
import com.yuhtin.lauren.startup.Startup;
import com.yuhtin.lauren.utils.UserUtil;
import lombok.val;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;

@CommandInfo(
        name = "createregister",
        type = CommandInfo.CommandType.CUSTOM_MESSAGES,
        args = {},
        description = "Criar a mensagem de registro de cargos"
)
public class RegisterCommand implements Command {

    @Override
    public void execute(CommandInteraction event, InteractionHook hook) {
        if (event.getMember() == null
                || !UserUtil.hasPermission(event.getMember(), hook, Permission.ADMINISTRATOR)) return;

        val action = event.getTextChannel().sendMessage(":flag_br: - Portuguese\n" +
                "Olá jogadores, para realizar seu cadastro reaja abaixo desta mensagem qual o seu sexo respectivamente\n" +
                "\n" +
                "<:nao_pertubar:703089222185386056> Menino\n" +
                "<:live:704293077623504957> Menina\n" +
                "\n" +
                ":flag_us: - English\n" +
                "Hello players, to complete your registration please react below this message what is your gender respectively\n" +
                "\n" +
                "<:nao_pertubar:703089222185386056> Boy\n" +
                "<:live:704293077623504957> Girl\n" +
                "\n" +
                "@everyone");

        action.queue(message -> {
            message.addReaction(":nao_pertubar:703089222185386056").queue();
            message.addReaction(":live:704293077623504957").queue();
            Startup.getLauren().getConfig().setResgistrationId(message.getIdLong());
        });

        hook.setEphemeral(true).sendMessage("👍 Fiz a mensagem pica ai").queue();
    }
}

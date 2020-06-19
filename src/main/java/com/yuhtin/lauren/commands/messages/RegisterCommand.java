package com.yuhtin.lauren.commands.messages;

import com.yuhtin.lauren.application.Lauren;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.yuhtin.lauren.models.annotations.CommandHandler;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import com.yuhtin.lauren.utils.helper.Utilities;

@CommandHandler(name = "createregister", type = CommandHandler.CommandType.CUSTOM_MESSAGES, description = "Criar a mensagem de registro de cargos")
public class RegisterCommand extends Command {
    public RegisterCommand() {
        this.name = "createregister";
    }

    @Override
    protected void execute(CommandEvent event) {
        event.getMessage().delete().queue();
        if (!Utilities.isPermission(event.getMember(), event.getChannel(), Permission.ADMINISTRATOR)) return;
        MessageAction action = event.getChannel().sendMessage(":flag_br: - Portuguese\n" +
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
            Lauren.config.setResgistrationId(message.getIdLong());
            message.addReaction(":nao_pertubar:703089222185386056").queue();
            message.addReaction(":live:704293077623504957").queue();
        });
    }
}

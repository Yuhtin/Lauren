package com.yuhtin.lauren.commands.impl.messages;

import com.yuhtin.lauren.commands.Command;
import com.yuhtin.lauren.commands.CommandInfo;
import com.yuhtin.lauren.commands.CommandType;
import lombok.val;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;

@CommandInfo(
        name = "createregister",
        type = CommandType.CUSTOM_MESSAGES,
        args = {},
        description = "Criar a mensagem de registro de cargos",
        permissions = { Permission.ADMINISTRATOR }
)
public class RegisterCommand implements Command {

    @Override
    public void execute(CommandInteraction event, InteractionHook hook) {
        if (event.getMember() == null) return;

        val action = ((MessageChannel) event.getChannel()).sendMessage("""
                :flag_br: - Portuguese
                Olá jogadores, para realizar seu cadastro reaja abaixo desta mensagem qual o seu sexo respectivamente

                <:nao_pertubar:703089222185386056> Menino
                <:live:704293077623504957> Menina

                :flag_us: - English
                Hello players, to complete your registration please react below this message what is your gender respectively

                <:nao_pertubar:703089222185386056> Boy
                <:live:704293077623504957> Girl

                @everyone""");

        action.queue(message -> {
            message.addReaction(Emoji.fromUnicode(":nao_pertubar:703089222185386056")).queue();
            message.addReaction(Emoji.fromUnicode(":live:704293077623504957")).queue();
        });

        hook.setEphemeral(true).sendMessage("👍 Fiz a mensagem pica ai").queue();
    }
}

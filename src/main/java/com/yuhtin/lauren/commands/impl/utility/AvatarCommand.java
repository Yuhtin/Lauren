package com.yuhtin.lauren.commands.impl.utility;

import com.yuhtin.lauren.commands.Command;
import com.yuhtin.lauren.commands.CommandInfo;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;

import java.time.Instant;

@CommandInfo(
        name = "avatar",
        type = CommandInfo.CommandType.UTILITY,
        description = "Espiar a imagem de outro usuário OjO",
        args = {
                "[@user]-Usuário que você quer ver o avatar"
        }
)
public class AvatarCommand implements Command {

    @Override
    public void execute(CommandInteraction event, InteractionHook hook) throws Exception {
        val userOption = event.getOption("user");
        val target = userOption == null ? event.getMember() : userOption.getAsMember();

        val avatarUrl = target.getUser().getAvatarUrl();
        if (avatarUrl == null) {
            hook.setEphemeral(true).sendMessage("Ops, este jogador não possui imagem de perfil").queue();
            return;
        }

        val embed = new EmbedBuilder()
                .setAuthor("Avatar de " + target.getUser().getName(), "https://google.com", avatarUrl)
                .setImage(target.getUser().getAvatarUrl())
                .setColor(target.getColor())
                .setFooter("Comando usado por " + event.getUser().getAsTag(), event.getUser().getAvatarUrl())
                .setTimestamp(Instant.now());

        hook.setEphemeral(true).sendMessageEmbeds(embed.build()).queue();
    }

}

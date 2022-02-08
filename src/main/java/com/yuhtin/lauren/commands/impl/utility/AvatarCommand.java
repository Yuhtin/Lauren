package com.yuhtin.lauren.commands.impl.utility;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.yuhtin.lauren.commands.Command;
import com.yuhtin.lauren.commands.CommandData;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

@CommandData(
        name = "avatar",
        type = CommandData.CommandType.UTILITY,
        description = "Espiar a imagem de outro usuário OjO",
        alias = {"image", "imagem"})
public class AvatarCommand implements Command {

    @Override
    protected void execute(CommandEvent event) {
        Member target = event.getMessage().getMentionedMembers().isEmpty()
                ? event.getMember()
                : event.getMessage().getMentionedMembers().get(0);

        String avatarUrl = target.getUser().getAvatarUrl();
        if (avatarUrl == null) {
            event.getChannel().sendMessage("Ops, este jogador não possui imagem de perfil").queue((m) -> m.delete().queueAfter(5, TimeUnit.SECONDS));
            event.getMessage().delete().queueAfter(5, TimeUnit.SECONDS);
            return;
        }

        EmbedBuilder embed = new EmbedBuilder()
                .setAuthor("Avatar de " + target.getUser().getName(), "https://google.com", avatarUrl)
                .setImage(target.getUser().getAvatarUrl())
                .setColor(target.getColor())
                .setFooter("Comando usado por " + event.getMember().getNickname(), event.getAuthor().getAvatarUrl())
                .setTimestamp(Instant.now());

        event.getMessage().getChannel().sendMessage(embed.build()).queue();
    }
}

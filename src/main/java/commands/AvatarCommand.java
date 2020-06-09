package commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class AvatarCommand extends Command {
    public AvatarCommand() {
        this.name = "avatar";
        this.aliases = new String[]{"image", "imagem"};
        this.help = "Ver o avatar de um jogador";
    }

    @Override
    protected void execute(CommandEvent event) {
        Member target = event.getMessage().getMentionedMembers().size() < 1 ? event.getMember() : event.getMessage().getMentionedMembers().get(0);

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

package com.yuhtin.lauren.commands.impl.help;

import com.yuhtin.lauren.commands.Command;
import com.yuhtin.lauren.commands.CommandInfo;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;

import java.time.Instant;

@CommandInfo(
        name = "servidor",
        type = CommandInfo.CommandType.HELP,
        description = "Visualizar as informações deste servidor"
)
public class ServerInfoCommand implements Command {

    @Override
    public void execute(CommandInteraction event, InteractionHook hook) throws Exception {
        val guildId = event.getGuild().getId();
        val roleSize = event.getGuild().getRoles().size() + "";

        val owner = event.getGuild().getOwner();
        val ownerName = owner == null ? "Ningúem" : owner.getUser().getName();
        val ownerId = owner == null ? "0" : owner.getId();

        val textChannels = event.getGuild().getTextChannels().size();
        val voiceChannels = event.getGuild().getVoiceChannels().size();
        val channelsSize = textChannels + voiceChannels;

        val embedBuilder = new EmbedBuilder()
                .setColor(event.getMember().getColor())
                .setAuthor(event.getGuild().getName(), "https://google.com", event.getGuild().getIconUrl())
                .setThumbnail(event.getGuild().getIconUrl())

                .addField("💻 ID", guildId, true)
                .addField("🧶 Cargos", roleSize, true)
                .addField("👑 Dono", "`" + ownerName + "`\n(" + ownerId + ")", true)
                .addField("💬 Canais (" + channelsSize + ")", "📝 **Texto:** " + textChannels + "\n🗣 **Voz:** " + voiceChannels, true)
                .addField("📆 Criado em", DateUtil.format(event.getGuild().getTimeCreated().toEpochSecond()), true)
                .addField("✨ Você entrou em", DateUtil.format(event.getMember().getTimeJoined().toEpochSecond()), true)

                .setFooter("Comando usado as", event.getUser().getAvatarUrl())
                .setTimestamp(Instant.now());

        hook.setEphemeral(true).sendMessageEmbeds(embedBuilder.build()).queue();
    }

}

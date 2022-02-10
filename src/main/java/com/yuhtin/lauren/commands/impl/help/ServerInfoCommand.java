package com.yuhtin.lauren.commands.impl.help;

import com.yuhtin.lauren.commands.Command;
import com.yuhtin.lauren.commands.CommandData;
import com.yuhtin.lauren.utils.TimeUtils;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.TextStyle;
import java.util.Locale;

@CommandData(
        name = "servidor",
        type = CommandData.CommandType.HELP,
        description = "Visualizar as informações deste servidor"
)
public class ServerInfoCommand implements Command {

    @Override
    public void execute(CommandInteraction event, InteractionHook hook) throws Exception {
        val guildId = event.getGuild().getId();
        val roleSize = event.getGuild().getRoles().size() + "";
        val creationDate = subtractTime(event.getGuild().getTimeCreated());
        val userDate = event.getMember() == null ? "Erro" : subtractTime(event.getMember().getTimeJoined());

        val owner = event.getGuild().getOwner();
        val ownerName = owner == null ? "Ningúem" : owner.getUser().getAsTag();
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
                .addField("📆 Criado em", creationDate, true)
                .addField("✨ Você entrou em", userDate, true)

                .setFooter("Comando usado as", event.getUser().getAvatarUrl())
                .setTimestamp(Instant.now());

        hook.setEphemeral(true).sendMessageEmbeds(embedBuilder.build()).queue();
    }

    private String subtractTime(OffsetDateTime before) {
        return before.getDayOfMonth() + " de " + before.getMonth().getDisplayName(TextStyle.SHORT, Locale.US) + ", "
                + before.getYear() + " às " + before.getHour() + ":" + before.getMinute() +
                " (" + TimeUtils.formatTime(System.currentTimeMillis() - before.toInstant().toEpochMilli()) + ")";
    }
}

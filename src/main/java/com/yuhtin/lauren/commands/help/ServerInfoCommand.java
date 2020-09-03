package com.yuhtin.lauren.commands.help;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.yuhtin.lauren.models.annotations.CommandHandler;
import com.yuhtin.lauren.utils.helper.MathUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.TextStyle;
import java.util.Locale;

@CommandHandler(
        name = "servidor",
        type = CommandHandler.CommandType.HELP,
        description = "Visualizar as informações deste servidor",
        alias = {"sinfo", "server", "servidor"})
public class ServerInfoCommand extends Command {

    @Override
    protected void execute(CommandEvent event) {
        String guildId = event.getGuild().getId();
        String roleSize = event.getGuild().getRoles().size() + "";
        String regionName = event.getGuild().getRegion().getName();
        String creationDate = subtractTime(event.getGuild().getTimeCreated());
        String userDate = event.getMessage().getMember() == null ? "Erro" : subtractTime(event.getMessage().getMember().getTimeJoined());

        Member owner = event.getGuild().getOwner();
        String ownerName = (owner == null ? "Ningúem" : owner.getUser().getName() + "#" + owner.getUser().getDiscriminator());
        String ownerId = (owner == null ? "0" : owner.getId());

        int textChannels = event.getGuild().getTextChannels().size();
        int voiceChannels = event.getGuild().getVoiceChannels().size();
        int channelsSize = textChannels + voiceChannels;

        int membersSize = event.getGuild().getMembers().size();
        int onlineMembers = 0, awayMembers = 0, bots = 0, members = 0, offlineMembers = 0, busyMembers = 0;
        for (Member member : event.getGuild().getMembers()) {
            if (member.getUser().isBot()) ++bots;
            else ++members;
            switch (member.getOnlineStatus()) {
                case ONLINE:
                    ++onlineMembers;
                    continue;
                case INVISIBLE:
                case UNKNOWN:
                case OFFLINE:
                    ++offlineMembers;
                    continue;
                case IDLE:
                    ++awayMembers;
                    continue;
                case DO_NOT_DISTURB:
                    ++busyMembers;
            }
        }


        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(event.getMember().getColor());
        embedBuilder.setAuthor(event.getGuild().getName(), "https://google.com", event.getGuild().getIconUrl());
        embedBuilder.setThumbnail(event.getGuild().getIconUrl());

        embedBuilder.addField("💻 ID", guildId, true);
        embedBuilder.addField("🧶 Cargos", roleSize, true);
        embedBuilder.addField("👑 Dono", "`" + ownerName + "`\n(" + ownerId + ")", true);
        embedBuilder.addField("🌎 Região", regionName, true);
        embedBuilder.addField("💬 Canais (" + channelsSize + ")", "📝 **Texto:** " + textChannels + "\n🗣 **Voz:** " + voiceChannels, true);
        embedBuilder.addField("📆 Criado em", creationDate, true);
        embedBuilder.addField("✨ Entrei aqui em", userDate, true);
        embedBuilder.addField("🙍‍♂️ Membros (" + membersSize + ")",
                "<:online:703089222021808170> **Online:** " + onlineMembers + " | <:ausente:703089221774344224> **Ausente:** " + awayMembers + " |\n"
                        + "<:nao_pertubar:703089222185386056> **Ocupado:** " + busyMembers + " | <:offline:703089222243975218> **Offline:** " + offlineMembers + "\n"
                        + "🙋 **Pessoas:** " + members + "\n🤖 **Bots:** " + bots, true);

        embedBuilder.setFooter("Comando usado as", event.getAuthor().getAvatarUrl());
        embedBuilder.setTimestamp(Instant.now());

        event.getChannel().sendMessage(embedBuilder.build()).queue();
    }

    private String subtractTime(OffsetDateTime before) {
        return before.getDayOfMonth() + " de " + before.getMonth().getDisplayName(TextStyle.SHORT, Locale.US) + ", "
                + before.getYear() + " às " + before.getHour() + ":" + before.getMinute() +
                " (" + MathUtils.format(System.currentTimeMillis() - before.toInstant().toEpochMilli()) + ")";
    }
}

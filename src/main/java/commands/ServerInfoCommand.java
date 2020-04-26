package commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;

import java.time.OffsetDateTime;
import java.time.format.TextStyle;
import java.util.Locale;

public class ServerInfoCommand extends Command {
    public ServerInfoCommand() {
        this.name = "serverinfo";
        this.aliases = new String[]{"sinfo", "server"};
        this.help = "Informações sobre o servidor.";
    }

    @Override
    protected void execute(CommandEvent event) {
        String guildId = event.getGuild().getId();
        String roleSize = event.getGuild().getRoles().size() + "";
        String regionName = event.getGuild().getRegion().getName();
        String creationDate = subtractTime(OffsetDateTime.now(), event.getGuild().getTimeCreated());
        String userDate = event.getMessage().getMember() == null ? "Erro" : subtractTime(OffsetDateTime.now(), event.getMessage().getMember().getTimeJoined());

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

        event.getChannel().sendMessage(embedBuilder.build()).queue();
    }

    private String subtractTime(OffsetDateTime actual, OffsetDateTime before) {
        int year = actual.getYear() - before.getYear();
        int month = actual.getMonthValue() - before.getMonthValue();
        if (month < 0) {
            --year;
            month = 12 + month;
        }
        int day = actual.getDayOfMonth() - before.getDayOfMonth();
        if (day < 0) {
            --month;
            day = 30 + day;
        }
        int hours = actual.getHour() - before.getHour();
        if (hours < 0) {
            --day;
            hours = 24 + hours;
        }
        int minutes = actual.getMinute() - before.getMinute();
        if (minutes < 0) {
            --hours;
            minutes = 60 + minutes;
        }
        StringBuilder builder = new StringBuilder();
        if (year > 0) builder.append(year).append(" ").append(year > 1 ? "anos" : "ano").append(" ");
        if (month > 0) builder.append(month).append(" ").append(month > 1 ? "meses" : "mes").append(" ");
        if (day > 0) builder.append(day).append(" ").append(day > 1 ? "dias" : "dia").append(" ");
        if (hours > 0) builder.append(hours).append(" ").append(hours > 1 ? "horas" : "hora").append(" ");
        if (minutes > 0) builder.append(minutes).append(" ").append(minutes > 1 ? "minutos" : "minuto").append(" ");
        return before.getDayOfMonth() + " de " + before.getMonth().getDisplayName(TextStyle.SHORT, Locale.US) + ", "
                + before.getYear() + " às " + before.getHour() + ":" + before.getMinute() +
                " (" + builder.toString() + ")";
    }
}

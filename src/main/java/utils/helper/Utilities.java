package utils.helper;

import application.Lauren;
import enums.Rank;
import logger.Logger;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Utilities {

    public static boolean isPermission(Member member, MessageChannel channel, Permission permission) {
        if (!member.hasPermission(permission)) {
            MessageAction message = channel.sendMessage("<a:nao:704295026036834375> Você não tem permissão para usar esta função");
            message.queue((m) -> m.delete().queueAfter(5, TimeUnit.SECONDS));
            Logger.log("Failed to check permissions for user " + member.getUser().getName() + "#" + member.getUser().getDiscriminator());
            return false;
        }
        return true;
    }

    public static void setNick(Long userID, int level, Rank poolRank, Rank ludoRank) {
        Member member = Lauren.bot.getGuilds().get(0).getMemberById(userID);
        if (member == null) return;

        String nickname = member.getNickname();
        if (nickname == null) nickname = member.getEffectiveName();
        if (nickname.contains("] ")) nickname = nickname.split("] ")[1];

        nickname = Lauren.config.formatNickname.replace("@level", "" + level) + nickname;
        if (nickname.length() > 32) nickname = nickname.substring(0, 32);

        member.modifyNickname(nickname).queue();
    }

    public static String format(double valor) {
        DecimalFormat decimalFormat = new DecimalFormat("#.##", new DecimalFormatSymbols(Locale.US));
        return decimalFormat.format(valor);
    }

    public static String rolesToString(List<Role> roles) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < roles.size(); i++) {
            if (i != 0) builder.append(", ");
            builder.append(roles.get(i).getName());
        }

        return builder.toString();
    }

    public static String randomString() {
        StringBuilder sb = new StringBuilder();
        String a = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        int i;
        for (int t = 0; t < 15; t++) {
            i = new Random().nextInt(a.length());
            sb.append(a, i, i + 1);
        }

        return sb.toString();
    }
}

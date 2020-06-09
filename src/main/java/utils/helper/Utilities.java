package utils.helper;

import application.Lauren;
import logger.Logger;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

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

    public static void setNick(Long userID, int level) {
        Member member = Lauren.bot.getGuilds().get(0).getMemberById(userID);
        if (member == null) return;

        member.modifyNickname(Lauren.config.formatNickname.replace("@level", "" + level) + member.getNickname()).queue();
    }
}

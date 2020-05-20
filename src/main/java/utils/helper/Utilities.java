package utils.helper;

import logger.Logger;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

import java.util.concurrent.TimeUnit;

public class Utilities {

    public static boolean isPermission(Member member, MessageChannel channel, Permission permission) {
        if (!member.hasPermission(permission)) {
            MessageAction message = channel.sendMessage("<a:nao:704295026036834375> Você não tem permissão para usar este comando");
            message.queue((m) -> m.delete().queueAfter(5, TimeUnit.SECONDS));
            Logger.log("Failed to check permissions for user " + member.getUser().getName() + "#" + member.getUser().getDiscriminator());
            return false;
        }
        return true;
    }
}

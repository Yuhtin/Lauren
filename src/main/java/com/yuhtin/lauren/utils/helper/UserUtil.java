package com.yuhtin.lauren.utils.helper;

import com.google.inject.Inject;
import com.yuhtin.lauren.core.player.Player;
import com.yuhtin.lauren.models.objects.Config;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.val;
import lombok.var;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.exceptions.HierarchyException;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserUtil {

    @Inject private static Config config;
    @Inject private static JDA bot;

    public static boolean hasPermission(Member member, Message message, Permission permission, boolean showMessage) {
        if (!member.hasPermission(permission)) {
            if (!showMessage) return false;

            message.addReaction(":x:").queue();
            return false;
        }

        return true;
    }

    public static boolean isOwner(MessageChannel channel, User user, boolean showMessage) {

        if (config.getOwnerID() != user.getIdLong()) {

            if (!showMessage) return false;

            channel.sendMessage("<a:nao:704295026036834375> Você não tem permissão para usar esta função")
                    .delay(10, TimeUnit.SECONDS)
                    .flatMap(Message::delete)
                    .queue();

            return false;

        }

        return true;

    }

    public static void updateNickByLevel(Player player, int level) {

        if (player.isHideLevelOnNickname()) return;

        val member = bot.getGuilds().get(0).getMemberById(player.getUserID());
        if (member == null) return;

        var nickname = member.getNickname();
        if (nickname == null) nickname = member.getEffectiveName();
        if (nickname.contains("] ")) nickname = nickname.split("] ")[1];

        nickname = config.getFormatNickname().replace("@level", "" + level) + nickname;

        if (nickname.length() > 32) nickname = nickname.substring(0, 32);

        try { member.modifyNickname(nickname).queue(); } catch (HierarchyException ignored) { }

    }

    public static String getFullName(User user) {
        return user.getName() + "#" + user.getDiscriminator();
    }

    public static String rolesToString(List<Role> roles) {
        val builder = new StringBuilder();
        for (int i = 0; i < roles.size(); i++) {
            if (i != 0) builder.append(", ");
            builder.append(roles.get(i).getName());
        }

        return builder.toString();
    }

    public static boolean isDJ(Member member, MessageChannel channel, boolean message) {
        val isDJ = member.getRoles().stream().anyMatch(role -> role.getName().equalsIgnoreCase("DJ \uD83C\uDFB6"));

        if (!isDJ && message)
            channel.sendMessage("Ahhh, que pena \uD83D\uDC94 você não pode realizar essa operação").queue();
        return isDJ;
    }

    public static boolean isPrime(Member member) {
        if (member == null) return false;
        return member.getRoles().stream().filter(Objects::nonNull).anyMatch(role -> role.getIdLong() == 722116789055782912L);
    }


    public static String nonNullString(String value) {
        return value == null ? "Não informado" : value;
    }

}

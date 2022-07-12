package com.yuhtin.lauren.utils;

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
import net.dv8tion.jda.api.interactions.InteractionHook;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserUtil {

    @Inject private static Config config;
    @Inject private static JDA bot;

    public static boolean hasPermission(Member member, InteractionHook hook, Permission permission) {
        if (!member.hasPermission(permission)) {
            if (hook == null) return false;

            hook.sendMessage(":x: Sem permissão.").queue();
            return false;
        }

        return true;
    }

    public static boolean isOwner(User user, InteractionHook hook) {
        if (user == null || config.getOwnerID() != user.getIdLong()) {
            if (hook == null) return false;

            hook.sendMessage("<a:nao:704295026036834375> Você não tem permissão para usar esta função").queue();
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

        try {
            member.modifyNickname(nickname).queue();
        } catch (HierarchyException ignored) {
        }
    }

    public static String rolesToString(List<Role> roles) {
        val builder = new StringBuilder();
        for (int i = 0; i < roles.size(); i++) {
            if (i != 0) builder.append(", ");
            builder.append(roles.get(i).getName());
        }

        return builder.toString();
    }

    public static boolean isDJ(Member member, InteractionHook hook) {
        val isDJ = member.getRoles().stream().anyMatch(role -> role.getName().equalsIgnoreCase("DJ \uD83C\uDFB6"));
        if (!isDJ && hook != null) {
            hook.sendMessage("Ahhh, que pena \uD83D\uDC94 você não pode realizar essa operação").queue();
        }

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

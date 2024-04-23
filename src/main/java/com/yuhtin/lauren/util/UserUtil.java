package com.yuhtin.lauren.util;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.InteractionHook;

public class UserUtil {
    public static boolean hasPermission(Member member, InteractionHook hook, Permission permission) {
        return member.hasPermission(permission);
    }
}

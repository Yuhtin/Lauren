package com.yuhtin.lauren.module.impl.player.module;

import com.yuhtin.lauren.Lauren;
import com.yuhtin.lauren.config.YamlConfiguration;
import com.yuhtin.lauren.module.ConfigurableModule;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

public class PlayerModule extends ConfigurableModule {

    @Override
    public boolean setup(Lauren lauren) throws Exception {
        setConfig(YamlConfiguration.load("features/player.yml"));
        return true;
    }

    public boolean isDJ(Member member) {
        long value = getConfig().getNumber("roles.dj", 0L).longValue();
        return hasRole(member, value);
    }

    public boolean isPrime(Member member) {
        long value = getConfig().getNumber("roles.prime", 0L).longValue();
        return hasRole(member, value);
    }

    public boolean hasRole(Member member, long roleId) {
        if (roleId == 0) return false;

        for (Role role : member.getRoles()) {
            if (role.getIdLong() == roleId) {
                return true;
            }
        }

        return false;
    }

}

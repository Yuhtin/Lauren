package com.yuhtin.lauren.core.punish;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum PunishmentType {

    BAN("banido"),
    MUTE("silenciado"),
    CALLBLOCK("bloqueado de call's");

    @Getter private final String formated;
}

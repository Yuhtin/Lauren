package com.yuhtin.lauren.core.punish;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum PunishmentType {

    BAN("Ban"), MUTE("Mute"), CALLBLOCK("Sem call");

    private final String formated;
}

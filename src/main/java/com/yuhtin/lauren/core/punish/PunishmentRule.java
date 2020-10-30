package com.yuhtin.lauren.core.punish;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.concurrent.TimeUnit;

@AllArgsConstructor
public enum PunishmentRule {

    // Messages punishiment
    P11(PunishmentType.MUTE, TimeUnit.HOURS, 2),
    P12(PunishmentType.MUTE, TimeUnit.HOURS, 5),
    P13(PunishmentType.MUTE, TimeUnit.HOURS, 1),
    P14(PunishmentType.MUTE, TimeUnit.HOURS, 48),
    P15(PunishmentType.MUTE, TimeUnit.HOURS, 1),
    P16(PunishmentType.MUTE, TimeUnit.MINUTES, 30),
    P17(PunishmentType.MUTE, TimeUnit.HOURS, 2),

    // Channels proibittions
    P21(PunishmentType.MUTE, TimeUnit.HOURS, 1),
    P22(PunishmentType.MUTE, TimeUnit.HOURS, 3),

    // Other proibittions
    P31(PunishmentType.BAN, TimeUnit.MINUTES, 0),
    P32(PunishmentType.MUTE, TimeUnit.HOURS, 1),
    P33(PunishmentType.BAN, TimeUnit.MINUTES, 0),

    // Call proibittions
    P41(PunishmentType.CALLBLOCK, TimeUnit.HOURS, 1),
    P42(PunishmentType.CALLBLOCK, TimeUnit.HOURS, 3),
    P43(PunishmentType.CALLBLOCK, TimeUnit.HOURS, 5),
    P44(PunishmentType.CALLBLOCK, TimeUnit.HOURS, 2);

    @Getter private final PunishmentType type;
    private final TimeUnit timeUnit;
    private final long time;

    public long getPunishTime() {
        return timeUnit.toMillis(time);
    }

    @Override
    public String toString() {
        return this.name().replace("P", "").substring(1);
    }
}

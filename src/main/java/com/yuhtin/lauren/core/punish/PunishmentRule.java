package com.yuhtin.lauren.core.punish;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.concurrent.TimeUnit;

@AllArgsConstructor
public enum PunishmentRule {

    // Messages punishiment
    P11(PunishmentType.MUTE, TimeUnit.HOURS, 2, "Flodar/Spammar mensagens"),
    P12(PunishmentType.MUTE, TimeUnit.HOURS, 5, "Enviar caracteres inválidos no chat"),
    P13(PunishmentType.MUTE, TimeUnit.HOURS, 1, "Críticas de forma mal-educada"),
    P14(PunishmentType.MUTE, TimeUnit.HOURS, 48, "Postagem de conteúdo como pornografia, violência explicita, apologia a drogas e etc..."),
    P15(PunishmentType.MUTE, TimeUnit.HOURS, 1, "Mensagens postadas em áreas erradas"),
    P16(PunishmentType.MUTE, TimeUnit.MINUTES, 30, "CapsLock excessivo"),
    P17(PunishmentType.MUTE, TimeUnit.HOURS, 2, "Proibido qualquer tipo de divulgação relacionada a outros servidores e ganhos na internet"),

    // Channels proibittions
    P21(PunishmentType.MUTE, TimeUnit.HOURS, 1, "Conversar em canais moderados"),
    P22(PunishmentType.MUTE, TimeUnit.HOURS, 3, "Usar comandos de bots em canais errados"),

    // Other proibittions
    P31(PunishmentType.BAN, TimeUnit.MINUTES, 0, "Conta-fake"),
    P32(PunishmentType.MUTE, TimeUnit.HOURS, 1, "Falta de respeito aos usuários"),
    P33(PunishmentType.BAN, TimeUnit.MINUTES, 0, "Nomes ofensivos"),

    // Call proibittions
    P41(PunishmentType.CALLBLOCK, TimeUnit.HOURS, 1, "Gritar em call"),
    P42(PunishmentType.CALLBLOCK, TimeUnit.HOURS, 3, "Programa de alteração de voz"),
    P43(PunishmentType.CALLBLOCK, TimeUnit.HOURS, 5, "Colocar música estourada"),
    P44(PunishmentType.CALLBLOCK, TimeUnit.HOURS, 2, "Ficar entrando e saindo da call");

    @Getter private final PunishmentType type;
    private final TimeUnit timeUnit;
    private final long time;
    @Getter private final String motive;

    public long getPunishTime() {
        return timeUnit.toMillis(time);
    }

    @Override
    public String toString() {
        String p = this.name().replace("P", "");
        String substring = p.substring(0, 1);

        return substring + "." + p.charAt(1);
    }
}

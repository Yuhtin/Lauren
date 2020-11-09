package com.yuhtin.lauren.models.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ItemType {

    KEY("Chave de Krypton"),
    RENAME_COMMAND("Comando $apelido"),
    TRADE_COMMAND("Comando $trade"),
    PRIME("Prime");


    @Getter private final String name;
}
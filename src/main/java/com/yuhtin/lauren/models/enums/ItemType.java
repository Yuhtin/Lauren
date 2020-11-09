package com.yuhtin.lauren.models.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ItemType {

    KEY("Chave de Krypton"),
    RENAME_COMMAND("Comando $apelido"),
    PRIME("Prime");


    @Getter private final String name;
}
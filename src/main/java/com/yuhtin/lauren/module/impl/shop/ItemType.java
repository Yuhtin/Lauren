package com.yuhtin.lauren.module.impl.shop;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ItemType {

    KEY("Chave de Krypton"),
    RENAME_COMMAND("Comando /apelido"),
    PRIME("Prime");

    private final String name;
}
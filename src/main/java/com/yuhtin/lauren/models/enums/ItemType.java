package com.yuhtin.lauren.models.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ItemType {

    KEY("Chave de Krypton");

    @Getter private final String name;
}
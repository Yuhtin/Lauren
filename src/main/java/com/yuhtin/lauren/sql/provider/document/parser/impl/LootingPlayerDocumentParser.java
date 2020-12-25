package com.yuhtin.lauren.sql.provider.document.parser.impl;

import com.yuhtin.minecraft.steellooting.api.player.LootingPlayer;
import com.yuhtin.minecraft.steellooting.sql.provider.document.Document;
import com.yuhtin.minecraft.steellooting.sql.provider.document.parser.DocumentParser;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class LootingPlayerDocumentParser implements DocumentParser<LootingPlayer> {

    @Getter private static final LootingPlayerDocumentParser instance = new LootingPlayerDocumentParser();

    @Override
    public LootingPlayer parse(Document document) {

        if (document.isEmpty()) return null;

        return LootingPlayer.builder()
                .lootingLimit(document.getNumber("lootingLimit").doubleValue())
                .build();
    }

}

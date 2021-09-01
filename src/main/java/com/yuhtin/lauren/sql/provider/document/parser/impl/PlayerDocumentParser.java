package com.yuhtin.lauren.sql.provider.document.parser.impl;

import com.yuhtin.lauren.core.player.Player;
import com.yuhtin.lauren.sql.provider.document.Document;
import com.yuhtin.lauren.sql.provider.document.parser.DocumentParser;
import com.yuhtin.lauren.utils.serialization.player.PlayerSerializer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.val;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PlayerDocumentParser implements DocumentParser<Player> {

    @Getter private static final PlayerDocumentParser instance = new PlayerDocumentParser();

    @Override
    public Player parse(Document document) {

        if (document.isEmpty()) return null;

        val player = PlayerSerializer.deserialize(document.getString("data"));
        player.setExperience(document.getNumber("xp").intValue());
        player.setAbbleToDaily(document.getBoolean("abbleToDaily"));

        return player;

    }

}

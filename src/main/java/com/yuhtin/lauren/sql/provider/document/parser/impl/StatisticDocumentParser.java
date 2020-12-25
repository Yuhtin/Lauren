package com.yuhtin.lauren.sql.provider.document.parser.impl;

import com.yuhtin.lauren.core.statistics.StatsInfo;
import com.yuhtin.lauren.sql.provider.document.Document;
import com.yuhtin.lauren.sql.provider.document.parser.DocumentParser;
import com.yuhtin.lauren.utils.serialization.Serializer;
import lombok.Getter;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
public class StatisticDocumentParser implements DocumentParser<StatsInfo> {

    @Getter private static final StatisticDocumentParser instance = new StatisticDocumentParser();


    @Override
    public StatsInfo parse(Document document) {

        return Serializer.getStats()
                .deserialize(document.getString("data"));

    }
}

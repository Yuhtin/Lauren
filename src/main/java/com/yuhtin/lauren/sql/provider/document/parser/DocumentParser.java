package com.yuhtin.lauren.sql.provider.document.parser;

import com.yuhtin.minecraft.steellooting.sql.provider.document.Document;

public interface DocumentParser<T> {

    T parse(Document document);

}

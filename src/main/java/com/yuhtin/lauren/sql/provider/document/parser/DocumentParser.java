package com.yuhtin.lauren.sql.provider.document.parser;

import com.yuhtin.lauren.sql.provider.document.Document;

public interface DocumentParser<T> {

    T parse(Document document);

}

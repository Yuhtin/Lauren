package com.yuhtin.lauren.sql.provider.document;

import com.yuhtin.lauren.sql.provider.document.parser.DocumentParser;
import com.yuhtin.lauren.utils.NumberUtils;

import java.util.HashMap;
import java.util.Map;

public class Document {

    private final Map<String, Object> valueMap = new HashMap<>();

    public boolean isEmpty() { return valueMap.isEmpty(); }

    public void insert(String key, Object value) {
        this.valueMap.put(key, value);
    }

    public String getString(String key) {
        Object result = this.valueMap.get(key);
        return result != null ? String.valueOf(result) : null;

    }

    public Number getNumber(String key) {
        return NumberUtils.createNumber(this.getString(key));
    }

    public boolean getBoolean(String key) {
        return Boolean.parseBoolean(this.getString(key));
    }

    public <T> T parse(DocumentParser<T> documentParser) {
        return documentParser.parse(this);
    }

}

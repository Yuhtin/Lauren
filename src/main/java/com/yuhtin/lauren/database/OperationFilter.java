package com.yuhtin.lauren.database;

import com.mongodb.client.model.Filters;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bson.conversions.Bson;

@Getter
@AllArgsConstructor
public enum OperationFilter {

    EQUALS("$eq"),
    NOT_EQUALS("$ne"),
    GREATER_THAN("$gt"),
    GREATER_THAN_OR_EQUAL("$gte"),
    LESS_THAN("$lt"),
    LESS_THAN_OR_EQUAL("$lte"),
    IN("$in"),
    NOT_IN("$nin"),
    EXISTS("$exists"),
    ALL("$all"),
    ;

    private final String value;

    public Bson filter(String key, Object value) {
        if (this == EQUALS) {
            return Filters.eq(key, value);
        } else if (this == NOT_EQUALS) {
            return Filters.ne(key, value);
        } else if (this == GREATER_THAN) {
            return Filters.gt(key, value);
        } else if (this == GREATER_THAN_OR_EQUAL) {
            return Filters.gte(key, value);
        } else if (this == LESS_THAN) {
            return Filters.lt(key, value);
        } else if (this == LESS_THAN_OR_EQUAL) {
            return Filters.lte(key, value);
        } else if (this == IN) {
            return Filters.in(key, value);
        } else if (this == NOT_IN) {
            return Filters.nin(key, value);
        } else if (this == EXISTS) {
            return Filters.exists(key, (boolean) value);
        } else if (this == ALL) {
            return Filters.all(key, value);
        }

        return Filters.empty();
    }

}

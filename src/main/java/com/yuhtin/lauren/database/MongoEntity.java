package com.yuhtin.lauren.database;

import com.yuhtin.lauren.util.FutureBuilder;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;

public interface MongoEntity<T> {

    Object getPrimaryKey();

    static <T extends MongoEntity<?>> FutureBuilder<T> find(Class<T> clazz, Object id) {
        return MongoOperation.bind(clazz)
                .filter(OperationFilter.EQUALS, "id", id)
                .find();
    }

    @NotNull
    static <T extends MongoEntity<?>> FutureBuilder<T> retrieve(Class<T> clazz, long id) {
        try {
            return MongoOperation.bind(clazz)
                    .filter(OperationFilter.EQUALS, "id", id)
                    .findOrCreate(clazz.getConstructor(long.class).newInstance(id));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    default void save() {
        saveOperation().queue();
    }

    default FutureBuilder<Boolean> saveOperation() {
        return MongoOperation.bind((Class<T>) getClass())
                .filter(OperationFilter.EQUALS, "id", getPrimaryKey())
                .insert((T) this);
    }

    default void invalidate() {
        MongoOperation.bind((Class<T>) getClass())
                .filter(OperationFilter.EQUALS, "id", getPrimaryKey())
                .delete()
                .queue();
    }
}
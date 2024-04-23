package com.yuhtin.lauren.database;

import com.mongodb.assertions.Assertions;
import com.mongodb.client.model.Filters;
import com.yuhtin.lauren.module.Module;
import com.yuhtin.lauren.util.FutureBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@RequiredArgsConstructor
@AllArgsConstructor
public class MongoOperation<T> {

    private final List<Bson> operations = new ArrayList<>();
    @Getter
    private MongoRepository<T> repository;

    public static <T> MongoOperation<T> bind(Class<T> clazz) {
        MongoModule mongoModule = Module.instance(MongoModule.class);
        if (mongoModule == null) {
            Logger.getLogger("Lauren").severe("MongoModule is not loaded!");
            return null;
        }

        return new MongoOperation<>(mongoModule.getRepository(clazz));
    }

    public MongoOperation<T> filter(OperationFilter filter, String key, Object value) {
        operations.add(filter.filter(key, value));
        return this;
    }

    public MongoOperation<T> and(Bson bson) {
        operations.add(bson);
        return this;
    }

    public FutureBuilder<Document> execute() {
        Assertions.notNull("repository", repository);
        return FutureBuilder.of(repository.retrieveDocument(operations));
    }

    public FutureBuilder<List<Document>> executeMany() {
        Assertions.notNull("repository", repository);
        return FutureBuilder.of(repository.retrieveDocuments(operations));
    }

    public FutureBuilder<T> find() {
        Assertions.notNull("repository", repository);
        return FutureBuilder.of(repository.retrieve(getOperations()));
    }

    public FutureBuilder<T> findOrCreate(T defaultObject) {
        Assertions.notNull("repository", repository);

        return FutureBuilder.of(repository.retrieve(getOperations())
                .thenApply(object -> {
                    if (object == null) {
                        repository.insert(getOperations(), defaultObject);
                        return defaultObject;
                    }

                    return object;
                }));
    }

    public FutureBuilder<List<Long>> retrieveIds() {
        Assertions.notNull("repository", repository);
        return FutureBuilder.of(repository.retrieveIds(getOperations()));
    }

    public FutureBuilder<List<T>> findMany() {
        Assertions.notNull("repository", repository);
        return FutureBuilder.of(repository.retrieveMany(getOperations()));
    }

    public FutureBuilder<Boolean> insert(T object) {
        Assertions.notNull("repository", repository);
        return FutureBuilder.of(repository.insert(getOperations(), object));
    }

    private Bson getOperations() {
        if (operations.isEmpty()) return null;
        return operations.stream().reduce(Filters::and).orElse(null);
    }

    public FutureBuilder<Void> delete() {
        Assertions.notNull("repository", repository);
        return FutureBuilder.of(repository.delete(getOperations()));
    }

}

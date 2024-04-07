package com.yuhtin.lauren.database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.ReplaceOptions;
import com.yuhtin.lauren.util.LoggerUtil;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Data
@RequiredArgsConstructor
public class MongoRepository<T> {

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(128);
    private static final Gson GSON;

    private static final ReplaceOptions REPLACE_OPTIONS = new ReplaceOptions().upsert(true);

    private final Class<T> clazz;
    @Getter
    private MongoCollection<Document> collection;

    static {
        GSON = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create();
    }

    @NotNull
    public CompletableFuture<T> retrieve(Bson filters) {
        return CompletableFuture.supplyAsync(() -> {
            Document document = collection.find(filters).first();
            if (document == null) return null;

            return GSON.fromJson(document.toJson(), clazz);
        });
    }

    public CompletableFuture<Boolean> exists(Bson filters) {
        return CompletableFuture.supplyAsync(() -> collection.find(filters).first() != null);
    }

    @NotNull
    public CompletableFuture<Boolean> insert(Bson filters, T object) {
        return CompletableFuture.supplyAsync(() -> {
            collection.replaceOne(filters, Document.parse(GSON.toJson(object)), REPLACE_OPTIONS);
            return true;
        });
    }

    @NotNull
    public CompletableFuture<List<T>> retrieveMany(Bson filters) {
        return CompletableFuture.supplyAsync(() -> {
            FindIterable<Document> documents = filters == null ? collection.find() : collection.find(filters);

            return documents.map(document -> GSON.fromJson(document.toJson(), clazz))
                    .into(new ArrayList<>());
        });
    }

    public CompletableFuture<Void> delete(Bson filters) {
        return CompletableFuture.runAsync(() -> collection.deleteOne(filters));
    }

    public CompletableFuture<List<Long>> retrieveIds(Bson filters) {
        return CompletableFuture.supplyAsync(() -> {
            FindIterable<Document> documents = filters == null ? collection.find() : collection.find(filters);

            List<Long> ids = new ArrayList<>();
            documents.forEach(document -> {
                long id = document.getLong("id");
                if (!ids.contains(id)) {
                    ids.add(id);
                }
            });

            return ids;
        });
    }

    public CompletableFuture<Document> retrieveDocument(List<Bson> operations) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return collection.aggregate(operations).first();
            } catch (Exception e) {
                LoggerUtil.printException(e);
                return null;
            }
        });
    }

    public CompletableFuture<List<Document>> retrieveDocuments(List<Bson> operations) {
        return CompletableFuture.supplyAsync(() -> {
            AggregateIterable<Document> documents = collection.aggregate(operations);

            List<Document> list = new ArrayList<>();
            documents.forEach(list::add);

            return list;
        });
    }
}

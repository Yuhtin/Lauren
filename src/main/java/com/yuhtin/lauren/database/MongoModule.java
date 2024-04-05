package com.yuhtin.lauren.database;

import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.yuhtin.lauren.Lauren;
import com.yuhtin.lauren.module.Module;
import com.yuhtin.lauren.util.EnvWrapper;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.logging.Logger;

@Getter
@Accessors(fluent = true)
public class MongoModule implements Module {

    private final HashMap<Class<?>, MongoRepository<?>> repositories = new HashMap<>();

    private Logger logger;
    private MongoClient client;
    private MongoDatabase database;

    @Override
    public boolean setup(Lauren lauren) throws Exception {
        this.logger = lauren.getLogger();

        try {
            connect(EnvWrapper.get("MONGO_URI"), EnvWrapper.get("MONGO_DATABASE"));
            return true;
        } catch (MongoException exception) {
            throw new Exception("Error trying to start MongoClient -> " + exception.getMessage());
        }
    }

    public void connect(String mongoUri, String databaseName) throws MongoException {
        try {
            this.client = MongoClients.create(mongoUri);
            this.client.startSession();

            this.database = client.getDatabase(databaseName);

            logger.info("Connected to database " + database.getName());
        } catch (MongoException e) {
            logger.severe("Can't connect with MongoDB!");
            logger.severe("Error: " + e.getMessage());
            database = null;
            client.close();
        }
    }

    public <T> void registerBinding(String collectionName, Class<T> clazz) {
        if (database == null) {
            logger.severe("MongoDB is not connected!");
            return;
        }

        if (clazz.isAssignableFrom(MongoEntity.class)) {
            try {
                clazz.getDeclaredField("id");
            } catch (NoSuchFieldException e) {
                logger.severe("##################### MONGO DATA PROBLEM #####################");
                logger.severe("The class " + clazz.getSimpleName() + " does not have a field named 'id'");
                logger.severe("This field is required to be the primary key of the collection");
                logger.severe("##############################################################");
            }
        }

        MongoRepository<T> repository = new MongoRepository<>(clazz);
        repository.setCollection(database.getCollection(collectionName));

        repositories.put(clazz, repository);

        logger.info("Connected and sync with " + collectionName + " collection!");
    }

    /**
     * Get a repository by representing class.
     *
     * @param clazz the class to get the repository for
     * @param <T>   the type of the repository
     * @return the repository for the class or null if it was not registered
     */
    @Nullable
    public <T> MongoRepository<T> getRepository(Class<T> clazz) {
        MongoRepository<T> repository = (MongoRepository<T>) repositories.get(clazz);
        if (repository == null) {
            throw new NullPointerException("Repository for " + clazz.getSimpleName() + " is not registered!");
        }

        return repository;
    }

    @Nullable
    public static MongoModule instance() {
        return Module.instance(MongoModule.class);
    }

}

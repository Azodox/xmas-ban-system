package fr.azodox.bansystem.mongo;

import com.mongodb.*;
import fr.azodox.bansystem.config.Configs;

public class Mongo {

    private final MongoClient mongoClient;

    public Mongo() {
        MongoCredential credential = MongoCredential.createCredential(
                Configs.DEFAULT_CONFIG.MONGODB_USERNAME,
                Configs.DEFAULT_CONFIG.MONGODB_AUTH_DATABASE,
                Configs.DEFAULT_CONFIG.MONGODB_PASSWORD.toCharArray()
        );

        MongoClientOptions options = MongoClientOptions.builder()
                .connectionsPerHost(10)
                .connectTimeout(100000)
                .maxWaitTime(100000)
                .socketTimeout(1000)
                .heartbeatConnectTimeout(600000)
                .writeConcern(WriteConcern.ACKNOWLEDGED)
        .build();

        this.mongoClient = new MongoClient(
                new ServerAddress(
                        Configs.DEFAULT_CONFIG.MONGODB_HOST,
                        Configs.DEFAULT_CONFIG.MONGODB_PORT),
                credential, options
        );
    }

    public MongoClient getMongoClient() {
        return mongoClient;
    }
}

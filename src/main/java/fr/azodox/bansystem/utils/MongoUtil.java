package fr.azodox.bansystem.utils;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import fr.azodox.bansystem.BanSystem;
import fr.azodox.bansystem.config.Configs;
import org.bson.Document;

public class MongoUtil {

    private final BanSystem main;
    private final MongoClient mongo;

    public MongoUtil(BanSystem main) {
        this.main = main;
        this.mongo = main.getMongo().getMongoClient();
    }

    public MongoCollection<Document> getMuted(){
        return mongo.getDatabase(BanSystem.DB_NAME).getCollection("muted");
    }

    public MongoCollection<Document> getBanned(){
        return mongo.getDatabase(BanSystem.DB_NAME).getCollection("banned");
    }

    public Document getByName(String name){
        final MongoClient mongo = main.getMongo().getMongoClient();
        final MongoDatabase database = mongo.getDatabase(Configs.DEFAULT_CONFIG.MONGODB_DATABASE);
        final MongoCollection<Document> accounts = database.getCollection("accounts");

        for (Document account : accounts.find()) {
            if(account.getString("name").equals(name)){
                return account;
            }
        }
        return null;
    }
}


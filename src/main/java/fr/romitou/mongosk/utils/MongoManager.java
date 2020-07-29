package fr.romitou.mongosk.utils;


import ch.njol.skript.Skript;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

public class MongoManager {

    private static MongoClient mongoClient;

    public static void createMongoClient(String connectionString, Boolean force) {
        if (force) mongoClient.close();
        try {
            mongoClient = MongoClients.create(connectionString);
            Skript.info("That's it. A new Mongo Client has successfully been created.");
        } catch (IllegalArgumentException ex) {
            Skript.error("Uh oh, something went wrong. " + ex.getMessage());
        }
    }

    public static MongoClient getMongoClient() {
        return mongoClient;
    }

    public void closeMongoClient() {
        mongoClient.close();
    }

}

package fr.romitou.mongosk.skript;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import fr.romitou.mongosk.codecs.CodecProvider;
import org.bson.codecs.configuration.CodecRegistries;

import java.util.HashMap;

public class MongoManager {

    private final static HashMap<String, MongoClient> clients = new HashMap<>();

    public static HashMap<String, MongoClient> getClients() {
        return clients;
    }

    public static MongoClient getClient(String name) {
        return clients.get(name);
    }

    public static void addClient(MongoClient client, String name) {
        clients.put(name, client);
    }

    public static void removeClient(MongoClient client) {
        clients.values().remove(client);
    }

    public static MongoClient buildClient(String uri) {
        return MongoClients.create(MongoClientSettings
                .builder()
                .applyConnectionString(new ConnectionString(uri))
                .codecRegistry(CodecRegistries.fromProviders(MongoClientSettings.getDefaultCodecRegistry(), new CodecProvider()))
                .build()
        );
    }

}

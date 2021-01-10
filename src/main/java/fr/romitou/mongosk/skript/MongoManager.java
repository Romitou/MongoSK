package fr.romitou.mongosk.skript;

import ch.njol.skript.Skript;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import fr.romitou.mongosk.MongoSK;
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

    public static void buildClient(String uri, String clientName) {
        try {
            MongoClientSettings.Builder settings;
            if (MongoSK.getConfigFile().getBoolean("experimental.codecs", false))
                settings = MongoClientSettings.builder()
                        .applyConnectionString(new ConnectionString(uri))
                        .codecRegistry(CodecRegistries.fromProviders(MongoClientSettings.getDefaultCodecRegistry(), new CodecProvider()));
            else
                settings = MongoClientSettings.builder()
                        .applyConnectionString(new ConnectionString(uri));
            addClient(MongoClients.create(settings.build()), clientName);
        } catch (IllegalArgumentException ex) {
            Skript.error("Something went wrong during the '" + clientName + "' client creation. " + ex.getMessage());
        }
    }

}

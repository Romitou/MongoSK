package fr.romitou.mongosk.skript;

import com.mongodb.client.MongoClient;

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

}

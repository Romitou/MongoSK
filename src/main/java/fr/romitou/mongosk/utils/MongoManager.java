package fr.romitou.mongosk.utils;

import ch.njol.skript.Skript;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

public class MongoManager {

    private static MongoClient mongoClient;

    /**
     * @param connectionString The connection address.
     */
    public static void createClient(String connectionString) {
        try {
            mongoClient = MongoClients.create(connectionString);
        } catch (IllegalArgumentException ex) {
            // We directly print the MongoDB error to the user.
            Skript.error("Uh oh, something went wrong. " + ex.getLocalizedMessage());
        }
    }

    public static MongoClient getClient() {
        return mongoClient;
    }

    /*
     * To check if the mongoClient is connected, we ask him to list databases.
     * If an error is catching, we deduct either the host is not reachable or
     * the client is not connected. TODO: Improve the #isConnected() method.
     */
    public static Boolean isConnected() {
        try {
            mongoClient.listDatabases();
            return true;
        } catch (NullPointerException ex) {
            return false;
        }
    }

    public static void queryError() {
        Skript.error("You cannot make a query while you're not connected to a host!");
    }

    /**
     * @param ex The exception.
     */
    public static void queryError(Exception ex) {
        Skript.error("Oops! Please check either host is reachable or collection and database exist (" + ex.getLocalizedMessage() + ").");
    }

}

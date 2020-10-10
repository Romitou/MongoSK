package fr.romitou.mongosk.skript.events;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.util.SimpleEvent;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import fr.romitou.mongosk.skript.events.bukkit.*;
import org.bson.Document;

public class SimpleEvents {
    static {

        // -- Client Close Event --
        Skript.registerEvent("Mongo Client Close",
                SimpleEvent.class,
                ClientCloseEvent.class,
                "[mongo[db]] client clos(e|ing)")
                .description("Called when a Mongo client is closed.")
                .examples("on mongo client close:",
                        "\tbroadcast \"%event-client% has been closed. :(\"")
                .since("1.1.0");
        EventValues.registerEventValue(ClientCloseEvent.class, MongoClient.class, new Getter<MongoClient, ClientCloseEvent>() {
            @Override
            public MongoClient get(ClientCloseEvent e) {
                return e.getClient();
            }
        }, 0);

        // -- Client Create Event --
        Skript.registerEvent("Mongo Client Create",
                SimpleEvent.class,
                ClientCreateEvent.class,
                "[mongo[db]] client creat(e|ing)")
                .description("Called when a new Mongo client is created.")
                .examples("on mongo client create:",
                        "\tbroadcast \"%event-client% has been created.\"")
                .since("1.1.0");
        EventValues.registerEventValue(ClientCreateEvent.class, MongoClient.class, new Getter<MongoClient, ClientCreateEvent>() {
            @Override
            public MongoClient get(ClientCreateEvent e) {
                return e.getClient();
            }
        }, 0);

        // Collection Create Event
        Skript.registerEvent("Mongo Collection Create",
                SimpleEvent.class,
                CollectionCreateEvent.class,
                "[mongo[db]] collection creat(e|ing)")
                .description("Called when a Mongo collection is created.")
                .examples("on mongo collection create:",
                        "\tbroadcast \"%event-collection% has been created!\"")
                .since("1.1.0");
        EventValues.registerEventValue(CollectionCreateEvent.class, MongoCollection.class, new Getter<MongoCollection, CollectionCreateEvent>() {
            @Override
            public MongoCollection get(CollectionCreateEvent e) {
                return e.getCollection();
            }
        }, 0);

        // Collection Drop Event
        Skript.registerEvent("Mongo Collection Drop",
                SimpleEvent.class,
                CollectionDropEvent.class,
                "[mongo[db]] collection drop[ping]")
                .description("Called when a collection is dropped.")
                .examples("on mongo collection drop:",
                        "\tbroadcast \"%event-collection% has been dropped!\"")
                .since("1.1.0");
        EventValues.registerEventValue(CollectionDropEvent.class, MongoCollection.class, new Getter<MongoCollection, CollectionDropEvent>() {
            @Override
            public MongoCollection get(CollectionDropEvent e) {
                return e.getCollection();
            }
        }, 0);

        // Database Drop Event
        Skript.registerEvent("Mongo Database Drop",
                SimpleEvent.class,
                DatabaseDropEvent.class,
                "[mongo[db]] database drop[ping]")
                .description("Called when a database is dropped.")
                .examples("on mongo database drop:",
                        "\tbroadcast \"%event-database% has been dropped!\"")
                .since("1.1.0");
        EventValues.registerEventValue(DatabaseDropEvent.class, MongoDatabase.class, new Getter<MongoDatabase, DatabaseDropEvent>() {
            @Override
            public MongoDatabase get(DatabaseDropEvent e) {
                return e.getDatabase();
            }
        }, 0);

        // Document Save Event
        Skript.registerEvent("Mongo Document Saved",
                SimpleEvent.class,
                DocumentSaveEvent.class,
                "[mongo[db]] document sav(e|ing)")
                .description("Called when a document is saved.")
                .examples("on mongo document save:",
                        "\tbroadcast \"%value \"\"test\"\" from event-document%\"")
                .since("1.1.0");
        EventValues.registerEventValue(DocumentSaveEvent.class, Document.class, new Getter<Document, DocumentSaveEvent>() {
            @Override
            public Document get(DocumentSaveEvent e) {
                return e.getDocument();
            }
        }, 0);
    }
}

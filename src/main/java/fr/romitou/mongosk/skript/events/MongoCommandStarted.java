package fr.romitou.mongosk.skript.events;

import fr.romitou.mongosk.compat.EventRegistration;
import fr.romitou.mongosk.adapters.MongoSKAdapter;
import fr.romitou.mongosk.elements.MongoSKDocument;
import fr.romitou.mongosk.elements.MongoSKServer;
import org.bson.BsonDocument;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class MongoCommandStarted extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    static {
        EventRegistration.registerEvent("Mongo command started", MongoCommandStarted.class, "mongo[(db|sk)] command start[ed]");
        EventRegistration.registerEventValue(MongoCommandStarted.class, MongoSKServer.class, event -> event.getMongoSKServer());
        EventRegistration.registerEventValue(MongoCommandStarted.class, MongoSKDocument.class, event -> event.getCommand());
        EventRegistration.registerEventValue(MongoCommandStarted.class, String.class, event -> event.getDatabaseName());
    }

    private final MongoSKServer mongoSKServer;
    private final MongoSKDocument command;
    private final String databaseName;

    public MongoCommandStarted(MongoSKServer mongoSKServer, BsonDocument command, String databaseName) {
        this.mongoSKServer = mongoSKServer;
        this.command = new MongoSKDocument(MongoSKAdapter.bsonDocumentToDocument(command));
        this.databaseName = databaseName;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public MongoSKDocument getCommand() {
        return command;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public MongoSKServer getMongoSKServer() {
        return mongoSKServer;
    }
}

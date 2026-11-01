package fr.romitou.mongosk.skript.events;

import fr.romitou.mongosk.compat.EventRegistration;
import fr.romitou.mongosk.adapters.MongoSKAdapter;
import fr.romitou.mongosk.elements.MongoSKDocument;
import fr.romitou.mongosk.elements.MongoSKServer;
import org.bson.BsonDocument;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class MongoCommandSucceeded extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    static {
        EventRegistration.registerEvent("Mongo command succeeded", MongoCommandSucceeded.class, "mongo[(db|sk)] command (success|succeeded)");
        EventRegistration.registerEventValue(MongoCommandSucceeded.class, MongoSKServer.class, event -> event.getMongoSKServer());
        EventRegistration.registerEventValue(MongoCommandSucceeded.class, MongoSKDocument.class, event -> event.getCommand());
        EventRegistration.registerEventValue(MongoCommandSucceeded.class, Long.class, event -> event.getElapsedTime());
    }

    private final MongoSKServer mongoSKServer;
    private final MongoSKDocument command;
    private final Long elapsedTime;

    public MongoCommandSucceeded(MongoSKServer mongoSKServer, BsonDocument command, long elapsedTime) {
        this.mongoSKServer = mongoSKServer;
        this.command = new MongoSKDocument(MongoSKAdapter.bsonDocumentToDocument(command));
        this.elapsedTime = elapsedTime;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public MongoSKServer getMongoSKServer() {
        return mongoSKServer;
    }

    public MongoSKDocument getCommand() {
        return command;
    }

    public Long getElapsedTime() {
        return elapsedTime;
    }
}

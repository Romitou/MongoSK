package fr.romitou.mongosk.skript.events;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.util.SimpleEvent;
import ch.njol.skript.registrations.EventValues;
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
        Skript.registerEvent(
            "Mongo command succeeded",
            SimpleEvent.class,
            MongoCommandSucceeded.class,
            "mongo[(db|sk)] command (success|succeeded)"
        );

        EventValues.registerEventValue(MongoCommandSucceeded.class, MongoSKServer.class, MongoCommandSucceeded::getMongoSKServer, 0);
        EventValues.registerEventValue(MongoCommandSucceeded.class, MongoSKDocument.class, MongoCommandSucceeded::getCommand, 0);
        EventValues.registerEventValue(MongoCommandSucceeded.class, Long.class, MongoCommandSucceeded::getElapsedTime, 0);
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

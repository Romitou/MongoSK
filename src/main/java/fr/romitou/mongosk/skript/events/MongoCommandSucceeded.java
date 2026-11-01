package fr.romitou.mongosk.skript.events;

import ch.njol.skript.lang.util.SimpleEvent;
import fr.romitou.mongosk.adapters.MongoSKAdapter;
import fr.romitou.mongosk.elements.MongoSKDocument;
import fr.romitou.mongosk.elements.MongoSKServer;
import org.bson.BsonDocument;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.bukkit.lang.eventvalue.EventValue;
import org.skriptlang.skript.bukkit.lang.eventvalue.EventValueRegistry;
import org.skriptlang.skript.bukkit.registration.BukkitSyntaxInfos;
import org.skriptlang.skript.registration.SyntaxRegistry;

public class MongoCommandSucceeded extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    public static void register(SyntaxRegistry registry, EventValueRegistry eventValues) {
        registry.register(BukkitSyntaxInfos.Event.KEY, BukkitSyntaxInfos.Event.builder(SimpleEvent.class, "Mongo command succeeded")
            .addEvent(MongoCommandSucceeded.class)
            .addPattern("mongo[(db|sk)] command (success|succeeded)")
            .build());
        eventValues.register(EventValue.simple(MongoCommandSucceeded.class, MongoSKServer.class, MongoCommandSucceeded::getMongoSKServer));
        eventValues.register(EventValue.simple(MongoCommandSucceeded.class, MongoSKDocument.class, MongoCommandSucceeded::getCommand));
        eventValues.register(EventValue.simple(MongoCommandSucceeded.class, Long.class, MongoCommandSucceeded::getElapsedTime));
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

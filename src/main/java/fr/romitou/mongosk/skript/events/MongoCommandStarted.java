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

public class MongoCommandStarted extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    public static void register(SyntaxRegistry registry, EventValueRegistry eventValues) {
        registry.register(BukkitSyntaxInfos.Event.KEY, BukkitSyntaxInfos.Event.builder(SimpleEvent.class, "Mongo command started")
            .addEvent(MongoCommandStarted.class)
            .addPattern("mongo[(db|sk)] command start[ed]")
            .build());
        eventValues.register(EventValue.simple(MongoCommandStarted.class, MongoSKServer.class, MongoCommandStarted::getMongoSKServer));
        eventValues.register(EventValue.simple(MongoCommandStarted.class, MongoSKDocument.class, MongoCommandStarted::getCommand));
        eventValues.register(EventValue.simple(MongoCommandStarted.class, String.class, MongoCommandStarted::getDatabaseName));
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

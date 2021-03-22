package fr.romitou.mongosk.skript.events;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.util.SimpleEvent;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import fr.romitou.mongosk.adapters.MongoSKAdapter;
import fr.romitou.mongosk.elements.MongoSKDocument;
import fr.romitou.mongosk.elements.MongoSKServer;
import org.bson.BsonDocument;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class MongoCommandStarted extends Event {

    static {
        Skript.registerEvent(
            "Mongo command started",
            SimpleEvent.class,
            MongoCommandStarted.class,
            "mongo[(db|sk)] command start[ed]"
        );

        EventValues.registerEventValue(MongoCommandStarted.class, MongoSKServer.class, new Getter<MongoSKServer, MongoCommandStarted>() {
            @Nonnull
            @Override
            public MongoSKServer get(@Nonnull MongoCommandStarted event) {
                return event.getMongoSKServer();
            }
        }, 0);

        EventValues.registerEventValue(MongoCommandStarted.class, MongoSKDocument.class, new Getter<MongoSKDocument, MongoCommandStarted>() {
            @Nonnull
            @Override
            public MongoSKDocument get(@Nonnull MongoCommandStarted event) {
                return event.getCommand();
            }
        }, 0);

        EventValues.registerEventValue(MongoCommandStarted.class, String.class, new Getter<String, MongoCommandStarted>() {
            @Nonnull
            @Override
            public String get(@Nonnull MongoCommandStarted event) {
                return event.getDatabaseName();
            }
        }, 0);
    }

    private static final HandlerList HANDLERS = new HandlerList();

    private final MongoSKServer mongoSKServer;
    private final MongoSKDocument command;
    private final String databaseName;

    public MongoCommandStarted(MongoSKServer mongoSKServer, BsonDocument command, String databaseName) {
        this.mongoSKServer = mongoSKServer;
        this.command = new MongoSKDocument(MongoSKAdapter.bsonDocumentToDocument(command));
        this.databaseName = databaseName;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
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

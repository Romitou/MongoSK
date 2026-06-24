package fr.romitou.mongosk.skript.events;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.util.SimpleEvent;
import ch.njol.skript.registrations.EventValues;
import fr.romitou.mongosk.elements.MongoSKServer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class MongoCommandFailed extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    static {
        Skript.registerEvent(
            "Mongo command failed",
            SimpleEvent.class,
            MongoCommandFailed.class,
            "mongo[(db|sk)] command fail[ed]"
        );

        EventValues.registerEventValue(MongoCommandFailed.class, MongoSKServer.class, MongoCommandFailed::getMongoSKServer, 0);
        EventValues.registerEventValue(MongoCommandFailed.class, String.class, event -> event.getThrowable().getMessage(), 0);
        EventValues.registerEventValue(MongoCommandFailed.class, Long.class, MongoCommandFailed::getElapsedTime, 0);
    }

    private final MongoSKServer mongoSKServer;
    private final Throwable throwable;
    private final Long elapsedTime;

    public MongoCommandFailed(MongoSKServer mongoSKServer, Throwable throwable, long elapsedTime) {
        this.mongoSKServer = mongoSKServer;
        this.throwable = throwable;
        this.elapsedTime = elapsedTime;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public Long getElapsedTime() {
        return elapsedTime;
    }

    public MongoSKServer getMongoSKServer() {
        return mongoSKServer;
    }
}

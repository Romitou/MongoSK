package fr.romitou.mongosk.skript.events;

import fr.romitou.mongosk.compat.EventRegistration;
import fr.romitou.mongosk.elements.MongoSKServer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class MongoCommandFailed extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    static {
        EventRegistration.registerEvent("Mongo command failed", MongoCommandFailed.class, "mongo[(db|sk)] command fail[ed]");
        EventRegistration.registerEventValue(MongoCommandFailed.class, MongoSKServer.class, event -> event.getMongoSKServer());
        EventRegistration.registerEventValue(MongoCommandFailed.class, String.class, event -> event.getThrowable().getMessage());
        EventRegistration.registerEventValue(MongoCommandFailed.class, Long.class, event -> event.getElapsedTime());
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

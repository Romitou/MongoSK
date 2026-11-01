package fr.romitou.mongosk.skript.events;

import ch.njol.skript.lang.util.SimpleEvent;
import fr.romitou.mongosk.elements.MongoSKServer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.bukkit.lang.eventvalue.EventValue;
import org.skriptlang.skript.bukkit.lang.eventvalue.EventValueRegistry;
import org.skriptlang.skript.bukkit.registration.BukkitSyntaxInfos;
import org.skriptlang.skript.registration.SyntaxRegistry;

public class MongoCommandFailed extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    public static void register(SyntaxRegistry registry, EventValueRegistry eventValues) {
        registry.register(BukkitSyntaxInfos.Event.KEY, BukkitSyntaxInfos.Event.builder(SimpleEvent.class, "Mongo command failed")
            .addEvent(MongoCommandFailed.class)
            .addPattern("mongo[(db|sk)] command fail[ed]")
            .build());
        eventValues.register(EventValue.simple(MongoCommandFailed.class, MongoSKServer.class, MongoCommandFailed::getMongoSKServer));
        eventValues.register(EventValue.simple(MongoCommandFailed.class, String.class, event -> event.getThrowable().getMessage()));
        eventValues.register(EventValue.simple(MongoCommandFailed.class, Long.class, MongoCommandFailed::getElapsedTime));
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

package fr.romitou.mongosk.skript.events.bukkit;

import com.mongodb.client.MongoDatabase;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DatabaseDropEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final MongoDatabase database;

    public DatabaseDropEvent(MongoDatabase database) {
        this.database = database;
    }

    @SuppressWarnings("unused")
    public static HandlerList getHandlerList() {
        return handlers;
    }

    public MongoDatabase getDatabase() {
        return database;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}

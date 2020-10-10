package fr.romitou.mongosk.skript.events.bukkit;

import com.mongodb.client.MongoCollection;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CollectionDropEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final MongoCollection collection;

    public CollectionDropEvent(MongoCollection collection) {
        this.collection = collection;
    }

    @SuppressWarnings("unused")
    public static HandlerList getHandlerList() {
        return handlers;
    }

    public MongoCollection getCollection() {
        return collection;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}

package fr.romitou.mongosk.skript.events.bukkit;

import com.mongodb.client.MongoClient;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ClientCreateEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final MongoClient client;

    public ClientCreateEvent(MongoClient mongoClient) {
        this.client = mongoClient;
    }

    @SuppressWarnings("unused")
    public static HandlerList getHandlerList() {
        return handlers;
    }

    public MongoClient getClient() {
        return client;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}

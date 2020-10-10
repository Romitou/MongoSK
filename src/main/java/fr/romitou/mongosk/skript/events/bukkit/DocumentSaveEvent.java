package fr.romitou.mongosk.skript.events.bukkit;

import org.bson.Document;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DocumentSaveEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final Document document;

    public DocumentSaveEvent(Document document) {
        this.document = document;
    }

    @SuppressWarnings("unused")
    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Document getDocument() {
        return document;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}

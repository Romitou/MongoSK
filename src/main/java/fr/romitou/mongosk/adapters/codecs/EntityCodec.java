package fr.romitou.mongosk.adapters.codecs;

import fr.romitou.mongosk.adapters.MongoSKCodec;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

import javax.annotation.Nonnull;
import java.io.StreamCorruptedException;
import java.util.UUID;

public class EntityCodec implements MongoSKCodec<Entity> {
    @Nonnull
    @Override
    public Entity deserialize(Document document) throws StreamCorruptedException {
        String entityId = document.getString("entityId");
        if (entityId == null)
            throw new StreamCorruptedException("Cannot retrieve entity ID from document!");
        try {
            UUID uuid = UUID.fromString(entityId);
            Entity entity = Bukkit.getEntity(uuid);
            if (entity == null)
                throw new StreamCorruptedException("No entity was found with this ID (" + entityId + ")!");
            return entity;
        } catch (IllegalArgumentException ex) {
            throw new StreamCorruptedException(ex.getMessage());
        }
    }

    @Nonnull
    @Override
    public Document serialize(Entity entity) {
        Document document = new Document();
        document.put("entityId", String.valueOf(entity.getEntityId()));
        return document;
    }

    @Nonnull
    @Override
    public Class<? extends Entity> getReturnType() {
        return Entity.class;
    }

    @Nonnull
    @Override
    public String getName() {
        return "entity";
    }
}

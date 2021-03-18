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
        String id = document.getString("id");
        if (id == null)
            throw new StreamCorruptedException("Cannot retrieve id field from document!");
        try {
            UUID uuid = UUID.fromString(id);
            Entity entity = Bukkit.getEntity(uuid);
            if (entity == null)
                throw new StreamCorruptedException("Cannot parse given entity ID!");
            return entity;
        } catch (IllegalArgumentException ex) {
            throw new StreamCorruptedException(ex.getMessage());
        }
    }

    @Nonnull
    @Override
    public Document serialize(Entity entity) {
        Document document = new Document();
        document.put("id", entity.getUniqueId().toString());
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

package fr.romitou.mongosk.adapters.codecs;

import fr.romitou.mongosk.adapters.MongoSKCodec;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.io.StreamCorruptedException;
import java.util.UUID;

/**
 * Players are also entities, so they are stored with this codec. To keep the stored format (and filters on existing
 * documents) unchanged, offline players are retrieved on deserialization, which is why this codec returns an Object.
 */
public class EntityCodec implements MongoSKCodec<Object> {
    @NotNull
    @Override
    public Object deserialize(Document document) throws StreamCorruptedException {
        String id = document.getString("id");
        if (id == null)
            throw new StreamCorruptedException("Cannot retrieve id field from document!");
        try {
            UUID uuid = UUID.fromString(id);
            Entity entity = Bukkit.getPlayer(uuid);
            if (entity == null) {
                entity = Bukkit.getEntity(uuid);
            }
            if (entity != null)
                return entity;
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
            if (offlinePlayer.hasPlayedBefore())
                return offlinePlayer;
            throw new StreamCorruptedException("Cannot parse given entity ID!");
        } catch (IllegalArgumentException ex) {
            throw new StreamCorruptedException(ex.getMessage());
        }
    }

    @NotNull
    @Override
    public Document serialize(Object entity) {
        Document document = new Document();
        document.put("id", ((Entity) entity).getUniqueId().toString());
        return document;
    }

    @NotNull
    @Override
    public Class<?> getReturnType() {
        return Entity.class;
    }

    @NotNull
    @Override
    public String getName() {
        return "entity";
    }
}

package fr.romitou.mongosk.adapters.codecs;

import fr.romitou.mongosk.adapters.MongoSKCodec;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.io.StreamCorruptedException;
import java.util.UUID;

public class PlayerCodec implements MongoSKCodec<OfflinePlayer> {
    @NotNull
    @Override
    public OfflinePlayer deserialize(Document document) throws StreamCorruptedException {
        String rawUuid = document.getString("uuid");
        if (rawUuid == null)
            throw new StreamCorruptedException("Cannot retrieve UUID field from document!");
        try {
            UUID uuid = UUID.fromString(rawUuid);
            return Bukkit.getOfflinePlayer(uuid);
        } catch (IllegalArgumentException ex) {
            throw new StreamCorruptedException(ex.getMessage());
        }
    }

    @NotNull
    @Override
    public Document serialize(OfflinePlayer player) {
        Document document = new Document();
        document.put("uuid", player.getUniqueId().toString());
        return document;
    }

    @NotNull
    @Override
    public Class<? extends OfflinePlayer> getReturnType() {
        return OfflinePlayer.class;
    }

    @NotNull
    @Override
    public String getName() {
        return "player";
    }
}

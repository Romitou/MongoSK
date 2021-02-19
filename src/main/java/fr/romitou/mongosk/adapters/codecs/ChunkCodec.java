package fr.romitou.mongosk.adapters.codecs;

import fr.romitou.mongosk.adapters.MongoSKCodec;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;

import javax.annotation.Nonnull;
import java.io.StreamCorruptedException;

public class ChunkCodec implements MongoSKCodec<Chunk> {
    @Nonnull
    @Override
    public Chunk deserialize(Document document) throws StreamCorruptedException {
        Integer x = document.getInteger("x"),
            z = document.getInteger("z");
        String worldName = document.getString("world");
        if (x == null || z == null || worldName == null)
            throw new StreamCorruptedException("Cannot retrieve x, z or world fields from document!");
        World world = Bukkit.getWorld(worldName);
        if (world == null)
            throw new StreamCorruptedException("Cannot parse given world name!");
        return world.getChunkAt(x, z);
    }

    @Nonnull
    @Override
    public Document serialize(Chunk chunk) {
        Document document = new Document();
        document.put("x", chunk.getX());
        document.put("z", chunk.getZ());
        document.put("world", chunk.getWorld().getName());
        return document;
    }

    @Nonnull
    @Override
    public Class<? extends Chunk> getReturnType() {
        return Chunk.class;
    }

    @Nonnull
    @Override
    public String getName() {
        return "chunk";
    }
}

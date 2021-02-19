package fr.romitou.mongosk.adapters.codecs;

import fr.romitou.mongosk.adapters.MongoSKCodec;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;

import javax.annotation.Nonnull;
import java.io.StreamCorruptedException;

public class BlockCodec implements MongoSKCodec<Block> {
    @Nonnull
    @Override
    public Block deserialize(Document document) throws StreamCorruptedException {
        String worldName = document.getString("world");
        int x = document.getInteger("x"),
            y = document.getInteger("y"),
            z = document.getInteger("z");
        if (worldName == null)
            throw new StreamCorruptedException("Cannot retrieve x, y, z fields and world field from document!");
        World world = Bukkit.getWorld(worldName);
        if (world == null)
            throw new StreamCorruptedException("Cannot parse given world name!");
        return world.getBlockAt(x, y, z);
    }

    @Nonnull
    @Override
    public Document serialize(Block block) {
        Document document = new Document();
        document.put("x", block.getX());
        document.put("y", block.getY());
        document.put("z", block.getZ());
        document.put("world", block.getWorld().getName());
        return document;
    }

    @Nonnull
    @Override
    public Class<? extends Block> getReturnType() {
        return Block.class;
    }

    @Nonnull
    @Override
    public String getName() {
        return "block";
    }
}

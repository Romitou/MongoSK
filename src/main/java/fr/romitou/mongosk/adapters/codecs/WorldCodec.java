package fr.romitou.mongosk.adapters.codecs;

import fr.romitou.mongosk.adapters.MongoSKCodec;
import fr.romitou.mongosk.utils.WorldCache;
import org.bson.Document;
import org.bukkit.World;

import javax.annotation.Nonnull;
import java.io.StreamCorruptedException;

public class WorldCodec implements MongoSKCodec<World> {
    @Nonnull
    @Override
    public World deserialize(Document document) throws StreamCorruptedException {
        String name = document.getString("name");
        if (name == null)
            throw new StreamCorruptedException("Cannot retrieve name field from document!");
        World world = WorldCache.getWorld(name);
        if (world == null)
            throw new StreamCorruptedException("Cannot parse given world name!");
        return world;
    }

    @Nonnull
    @Override
    public Document serialize(World world) {
        Document document = new Document();
        document.put("name", world.getName());
        return document;
    }

    @Nonnull
    @Override
    public Class<? extends World> getReturnType() {
        return World.class;
    }

    @Nonnull
    @Override
    public String getName() {
        return "world";
    }
}

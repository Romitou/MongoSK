package fr.romitou.mongosk.adapters.codecs;

import fr.romitou.mongosk.adapters.MongoSKCodec;
import fr.romitou.mongosk.utils.WorldCache;
import org.bson.Document;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.io.StreamCorruptedException;

public class WorldCodec implements MongoSKCodec<World> {
    @NotNull
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

    @NotNull
    @Override
    public Document serialize(World world) {
        Document document = new Document();
        document.put("name", world.getName());
        return document;
    }

    @NotNull
    @Override
    public Class<? extends World> getReturnType() {
        return World.class;
    }

    @NotNull
    @Override
    public String getName() {
        return "world";
    }
}

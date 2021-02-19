package fr.romitou.mongosk.adapters.codecs;

import fr.romitou.mongosk.adapters.MongoSKCodec;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import javax.annotation.Nonnull;
import java.io.StreamCorruptedException;

public class LocationCodec implements MongoSKCodec<Location> {
    @Nonnull
    @Override
    public Location deserialize(Document document) throws StreamCorruptedException {
        Integer x = document.getInteger("x"),
            y = document.getInteger("y"),
            z = document.getInteger("z");
        String worldName = document.getString("world");
        if (x == null || y == null || z == null || worldName == null)
            throw new StreamCorruptedException("Cannot retrieve x, y, z fields or world field from document!");
        World world = Bukkit.getWorld(worldName);
        if (world == null)
            throw new StreamCorruptedException("Cannot parse given world name!");
        return new Location(world, x, y, z);
    }

    @Nonnull
    @Override
    public Document serialize(Location location) {
        Document document = new Document();
        document.put("x", location.getX());
        document.put("y", location.getY());
        document.put("z", location.getZ());
        document.put("world", location.getWorld().getName());
        return document;
    }

    @Nonnull
    @Override
    public Class<? extends Location> getReturnType() {
        return Location.class;
    }

    @Nonnull
    @Override
    public String getName() {
        return "location";
    }
}

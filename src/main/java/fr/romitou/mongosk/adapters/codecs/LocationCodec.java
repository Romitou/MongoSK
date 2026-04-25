package fr.romitou.mongosk.adapters.codecs;

import fr.romitou.mongosk.adapters.MongoSKCodec;
import fr.romitou.mongosk.utils.WorldCache;
import org.bson.Document;
import org.bukkit.Location;
import org.bukkit.World;

import javax.annotation.Nonnull;
import java.io.StreamCorruptedException;

public class LocationCodec implements MongoSKCodec<Location> {
    @Nonnull
    @Override
    public Location deserialize(Document document) throws StreamCorruptedException {
        Number x = document.get("x", Number.class),
            y = document.get("y", Number.class),
            z = document.get("z", Number.class);
        String worldName = document.getString("world");
        if (x == null || y == null || z == null || worldName == null)
            throw new StreamCorruptedException("Cannot retrieve x, y, z fields or world field from document!");

        World world = WorldCache.getWorld(worldName);
        if (world == null)
            throw new StreamCorruptedException("Cannot parse given world name!");

        Number yaw = document.get("yaw", Number.class);
        Number pitch = document.get("pitch", Number.class);
        return new Location(world, x.doubleValue(), y.doubleValue(), z.doubleValue(),
            yaw != null ? yaw.floatValue() : 0F,
            pitch != null ? pitch.floatValue() : 0F);
    }

    @Nonnull
    @Override
    public Document serialize(Location location) {
        Document document = new Document();
        document.put("x", location.getX());
        document.put("y", location.getY());
        document.put("z", location.getZ());
        document.put("yaw", location.getYaw());
        document.put("pitch", location.getPitch());
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

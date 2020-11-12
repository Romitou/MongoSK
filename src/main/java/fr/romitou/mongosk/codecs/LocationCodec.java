package fr.romitou.mongosk.codecs;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class LocationCodec implements Codec<Location> {
    @Override
    public Location decode(BsonReader reader, DecoderContext decoderContext) {
        reader.readStartDocument();
        final World world = Bukkit.getWorld(reader.readString("world"));
        final double x = reader.readDouble("x"),
                y = reader.readDouble("y"),
                z = reader.readDouble("z");
        final int yaw = reader.readInt32("yaw"),
                pitch = reader.readInt32("pitch");
        return new Location(world, x, y, z, yaw, pitch);
    }

    @Override
    public void encode(BsonWriter writer, Location location, EncoderContext encoderContext) {
        writer.writeStartDocument();
        final World world = location.getWorld();
        assert world != null;
        writer.writeString("world", world.getName());
        writer.writeDouble("x", location.getX());
        writer.writeDouble("y", location.getY());
        writer.writeDouble("z", location.getZ());
        writer.writeInt32("yaw", (int) location.getYaw());
        writer.writeInt32("pitch", (int) location.getPitch());
        writer.writeEndDocument();
    }

    @Override
    public Class<Location> getEncoderClass() {
        return Location.class;
    }
}

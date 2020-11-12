package fr.romitou.mongosk.codecs;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bukkit.Bukkit;
import org.bukkit.World;

public class WorldCodec implements Codec<World> {
    @Override
    public World decode(BsonReader reader, DecoderContext decoderContext) {
        reader.readStartDocument();
        final World world = Bukkit.getWorld(reader.readString());
        reader.readEndDocument();
        assert world != null;
        return world;
    }

    @Override
    public void encode(BsonWriter writer, World world, EncoderContext encoderContext) {
        writer.writeStartDocument();
        writer.writeString(world.getName());
        writer.writeEndDocument();
    }

    @Override
    public Class<World> getEncoderClass() {
        return World.class;
    }
}
